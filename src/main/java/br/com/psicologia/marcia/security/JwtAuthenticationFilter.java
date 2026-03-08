package br.com.psicologia.marcia.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.psicologia.marcia.model.Analitics;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.service.analytics.RequisicaoAnaliticaService;
import br.com.psicologia.marcia.service.analytics.StatusCapturingResponseWrapper;
import br.com.psicologia.marcia.service.analytics.SuporteAnalytics;
import br.com.psicologia.marcia.service.usuario.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro responsável por validar o JWT das requisições protegidas.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    @Autowired
    private RequisicaoAnaliticaService requisicaoAnaliticaService;

    @Autowired
    private SuporteAnalytics suporteAnalytics;

    /**
     * Construtor do filtro JWT.
     *
     * @param tokenService service de token
     * @param usuarioService service de usuário
     * @param requisicaoAnaliticaService service de analytics
     * @param suporteAnalytics suporte de analytics
     */
    public JwtAuthenticationFilter(
            TokenService tokenService,
            UsuarioService usuarioService,
            RequisicaoAnaliticaService requisicaoAnaliticaService,
            SuporteAnalytics suporteAnalytics) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
        this.requisicaoAnaliticaService = requisicaoAnaliticaService;
        this.suporteAnalytics = suporteAnalytics;
    }

    /**
     * Executa a interceptação e validação do token JWT.
     *
     * @param request requisição HTTP
     * @param response resposta HTTP
     * @param filterChain cadeia de filtros
     * @throws ServletException erro de servlet
     * @throws IOException erro de IO
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Recupera URI da requisição
        String uri = request.getRequestURI();

        // Recupera token do header Authorization
        String token = recuperarToken(request);

        System.out.println("[DEBUG] URI interceptada: " + uri + " - Método: " + request.getMethod());

        // Ignora qualquer rota que NÃO seja da API
        if (!uri.startsWith("/api")) {
            System.out.println("[INTERCEPTOR] Ignorando rota do frontend/estático: " + uri);
            filterChain.doFilter(request, response);
            return;
        }

        // Ignora rotas públicas da API
        if (uri.equals("/api/edit/user/redefinir-senha")
                || uri.equals("/api/edit/user/register")
                || uri.equals("/api/auth/login")
                || uri.equals("/api/auth/deslogar")) {

            System.out.println("[INTERCEPTOR] Ignorando requisição pública da API: " + uri);
            filterChain.doFilter(request, response);
            return;
        }

        // Se não houver token, bloqueia acesso às rotas protegidas
        if (token == null || token.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token ausente. É necessário estar autenticado.");
            return;
        }

        try {
            // Extrai login do token
            String login = tokenService.getSubject(token);

            // Valida token no TokenStore
            if (!TokenStore.tokenValido(login, token)) {
                String messageError = "Token presente, mas inválido ou expirado.";
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(messageError);
                System.err.print(messageError);
                return;
            }

            // Carrega usuário autenticado
            Usuario usuario = (Usuario) usuarioService.loadUserByUsername(login);

            // Monta autenticação do Spring Security
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            usuario,
                            null,
                            usuario.getAuthorities()
                    );

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Registra autenticação no contexto
            SecurityContextHolder.getContext().setAuthentication(auth);

            System.out.println("Usuário autenticado via JWT: " + login);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Acesso negado: " + e.getMessage());
            return;
        }

        // Início da medição de tempo da requisição
        long inicio = System.currentTimeMillis();

        // Wrapper para capturar status HTTP final
        StatusCapturingResponseWrapper responseWrapper = new StatusCapturingResponseWrapper(response);

        // Continua cadeia de filtros
        filterChain.doFilter(request, responseWrapper);

        // Fim da medição
        long fim = System.currentTimeMillis();
        long duracao = fim - inicio;

        // Cria registro analítico
        Analitics ra = new Analitics();

        // Preenche dados principais
        ra.setEndpoint(uri);
        ra.setMetodo(request.getMethod());
        ra.setDataHora(LocalDateTime.now());
        ra.setIp(suporteAnalytics.capturarIpCliente(request));
        ra.setDuracao(duracao);
        ra.setStatusHttp(responseWrapper.getStatus());
        ra.setUserAgent(request.getHeader("User-Agent"));

        // Recupera autenticação atual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Se houver usuário autenticado, registra no analytics
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof Usuario usuario) {
            ra.setUsuario(usuario.getUsername());
            ra.setRole(usuario.getRole());
        }

        // Define endpoint e objetivo
        String endpoint = request.getRequestURI();
        ra.setEndpoint(endpoint);
        ra.setObjective(suporteAnalytics.mapearObjetivo(endpoint));

        // Define resumo da resposta
        ra.setRespostaCurta(responseWrapper.getStatus() == 200 ? "OK" : "Erro " + responseWrapper.getStatus());

        // Salva analytics
        requisicaoAnaliticaService.salvar(ra);
    }

    /**
     * Recupera o token Bearer do header Authorization.
     *
     * @param request requisição HTTP
     * @return token JWT ou null
     */
    private String recuperarToken(HttpServletRequest request) {
        // Obtém o header Authorization
        String authHeader = request.getHeader("Authorization");

        // Verifica se contém Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }

        return null;
    }
}