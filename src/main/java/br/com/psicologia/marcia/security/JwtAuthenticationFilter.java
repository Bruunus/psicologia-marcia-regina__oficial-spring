package br.com.psicologia.marcia.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.psicologia.marcia.model.Analitics;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.service.analytics.RequisicaoAnaliticaService;
import br.com.psicologia.marcia.service.analytics.StatusCapturingResponseWrapper;
import br.com.psicologia.marcia.service.analytics.SuporteAnalytics;
import br.com.psicologia.marcia.service.usuario.UsuarioService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
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

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String token = recuperarToken(request);

        System.out.println(
                "[DEBUG] URI interceptada: " + uri
                + " - Método: " + request.getMethod()
        );

        // Ignora qualquer rota que NÃO seja da API.
        if (!uri.startsWith("/api")) {
            System.out.println(
                    "[INTERCEPTOR] Ignorando rota do frontend/estático: "
                    + uri
            );

            filterChain.doFilter(request, response);
            return;
        }

        // Ignora rotas públicas da API.
        if (uri.equals("/api/edit/user/redefinir-senha")
                || uri.equals("/api/edit/user/register")
                || uri.equals("/api/auth/login")
                || uri.equals("/api/auth/deslogar")) {

            System.out.println(
                    "[INTERCEPTOR] Ignorando requisição pública da API: "
                    + uri
            );

            filterChain.doFilter(request, response);
            return;
        }

        if (token == null || token.isBlank()) {
            responderErro(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token ausente. É necessário estar autenticado."
            );
            return;
        }

        // Separa a validação do JWT do carregamento do usuário.
        final String login;

        try {
            login = tokenService.getSubject(token);

        } catch (RuntimeException e) {
            if (erroDeToken(e)) {
                responderErro(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Token inválido ou expirado."
                );
            } else {
                logger.error("Falha interna ao validar o token.", e);

                responderErro(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Falha interna ao validar a sessão."
                );
            }

            return;
        }

        if (login == null
                || login.isBlank()
                || !TokenStore.tokenValido(login, token)) {

            responderErro(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token presente, mas inválido ou expirado."
            );
            return;
        }

        try {
            Usuario usuario =
                    (Usuario) usuarioService.loadUserByUsername(login);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            usuario,
                            null,
                            usuario.getAuthorities()
                    );

            auth.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            System.out.println("Usuário autenticado via JWT: " + login);

        } catch (UsernameNotFoundException e) {
            responderErro(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Usuário da sessão não encontrado."
            );
            return;

        } catch (RuntimeException e) {
            // Inclui falhas de banco no carregamento do usuário.
            logger.error("Falha interna ao carregar o usuário da sessão.", e);

            responderErro(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Falha interna ao validar a sessão."
            );
            return;
        }

        long inicio = System.currentTimeMillis();

        StatusCapturingResponseWrapper responseWrapper =
                new StatusCapturingResponseWrapper(response);

        // Falhas dos endpoints não são convertidas em 401 por este filtro.
        filterChain.doFilter(request, responseWrapper);

        long fim = System.currentTimeMillis();
        long duracao = fim - inicio;

        Analitics ra = new Analitics();

        ra.setEndpoint(uri);
        ra.setMetodo(request.getMethod());
        ra.setDataHora(LocalDateTime.now());
        ra.setIp(suporteAnalytics.capturarIpCliente(request));
        ra.setDuracao(duracao);
        ra.setStatusHttp(responseWrapper.getStatus());
        ra.setUserAgent(request.getHeader("User-Agent"));

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth != null
                && auth.isAuthenticated()
                && auth.getPrincipal() instanceof Usuario usuario) {

            ra.setUsuario(usuario.getUsername());
            ra.setRole(usuario.getRole());
        }

        String endpoint = request.getRequestURI();

        ra.setEndpoint(endpoint);
        ra.setObjective(suporteAnalytics.mapearObjetivo(endpoint));

        ra.setRespostaCurta(
                responseWrapper.getStatus() == 200
                        ? "OK"
                        : "Erro " + responseWrapper.getStatus()
        );

        requisicaoAnaliticaService.salvar(ra);
    }

    /**
     * Identifica falhas conhecidas do token, inclusive quando
     * encapsuladas pelo TokenService.
     *
     * Erros de configuração da chave ou outras falhas internas
     * não são classificados genericamente como autenticação inválida.
     */
    private boolean erroDeToken(Throwable erro) {
        Throwable causa = erro;

        while (causa != null) {
            if (causa instanceof ExpiredJwtException
                    || causa instanceof MalformedJwtException
                    || causa instanceof UnsupportedJwtException
                    || causa instanceof SignatureException
                    || causa instanceof PrematureJwtException) {
                return true;
            }

            causa = causa.getCause();
        }

        return false;
    }

    /**
     * Responde em UTF-8 e limpa a autenticação desta requisição.
     * Não remove o token registrado no TokenStore.
     */
    private void responderErro(
            HttpServletResponse response,
            int status,
            String mensagem) throws IOException {

        SecurityContextHolder.clearContext();

        response.setStatus(status);
        response.setContentType("text/plain");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(mensagem);
    }

    /**
     * Recupera o token Bearer do cabeçalho Authorization.
     */
    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }

        return null;
    }
}