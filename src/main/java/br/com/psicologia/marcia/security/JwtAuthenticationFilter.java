package br.com.psicologia.marcia.security;

import br.com.psicologia.marcia.repository.usuario.GerenciadorDeAcessoDeUsuarioRepository;
import br.com.psicologia.marcia.service.usuario.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalTime;

/**
 * Filtro JWT que intercepta todas as requisições HTTP para validar o token JWT,
 * autenticar o usuário e verificar se ele ainda está com sessão ativa.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final GerenciadorDeAcessoDeUsuarioRepository gerenciadoDeAcesso;


    /**
     * Construtor para injeção de dependências.
     */
    public JwtAuthenticationFilter(TokenService tokenService,
                                   UsuarioService usuarioService,
                                   GerenciadorDeAcessoDeUsuarioRepository acessoRepo) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
        this.gerenciadoDeAcesso = acessoRepo;
    }

    /**
     * Filtro responsável por interceptar todas as requisições HTTP para aplicar a autenticação baseada em JWT.
     * 
     * <p>Este filtro realiza as seguintes ações:</p>
     * <ul>
     *   <li>Ignora rotas públicas como <code>/auth/login</code> e <code>/auth/deslogar</code>, permitindo o acesso sem autenticação.</li>
     *   <li>Extrai o token JWT do cabeçalho <code>Authorization</code>.</li>
     *   <li>Valida se o token extraído está registrado no <code>TokenStore</code>.</li>
     *   <li>Se válido, recupera o login do usuário a partir do token e autentica o usuário no contexto do Spring Security.</li>
     *   <li>Se inválido, retorna um erro HTTP 401 (Unauthorized).</li>
     * </ul>
     * 
     * @param request  a requisição HTTP recebida
     * @param response a resposta HTTP que será enviada
     * @param filterChain a cadeia de filtros que será continuada caso a autenticação esteja correta
     * 
     * @throws ServletException se ocorrer um erro no processamento da requisição
     * @throws IOException se ocorrer um erro de I/O durante o filtro
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = recuperarToken(request);
        String uri = request.getRequestURI();

        // 🔐 PERMITIR ROTAS PÚBLICAS ANTES DE QUALQUER VALIDAÇÃO DE TOKEN
        if (uri.endsWith("/auth/login") || uri.endsWith("/auth/deslogar")) {
            System.out.println("[INTERCEPTOR] Ignorando requisição pública: " + uri);
            filterChain.doFilter(request, response);
            return;
        }

        if (token != null && !token.isBlank()) {
            try {
                String login = tokenService.getSubject(token);

                if (!TokenStore.tokenValido(login, token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token inválido ou sessão expirada.");
                    return;
                }

                UserDetails userDetails = usuarioService.loadUserByUsername(login);

                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("Usuário autenticado via JWT: " + login);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Acesso negado: " + e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }







    /**
     * Extrai o token JWT do cabeçalho Authorization.
     */
    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }
        return null;
    }
}
