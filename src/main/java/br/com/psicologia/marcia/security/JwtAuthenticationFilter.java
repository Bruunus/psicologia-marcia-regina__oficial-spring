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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Extrai o token do cabeçalho Authorization
        String token = recuperarToken(request);

        if (token != null && !token.isBlank()) {
            try {
                // Extrai o login (username) do token
                String login = tokenService.getSubject(token);

                // Verifica se o usuário está logado no banco
                boolean usuarioLogado = gerenciadoDeAcesso.existsByNomeAndStatusLogin(login, true);

                if (usuarioLogado) {

                    // Se a requisição for para /deslogar, realiza o logoff imediatamente
                    if (request.getRequestURI().endsWith("/deslogar")) {
                        try {
                            usuarioService.deslogar(login);
                            System.out.println("Status_login alterado para FALSE via filtro em /deslogar");	// temp
                        } catch (Exception ex) {
                            System.err.println("Erro ao tentar deslogar usuário no filtro: " + ex.getMessage());
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            response.getWriter().write("Erro ao deslogar usuário.");	//temp
                            return;
                        }
                    }

                    // Autentica o usuário normalmente
                    UserDetails userDetails = usuarioService.loadUserByUsername(login);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    System.out.println("Usuário autenticado via filtro: " + login);

                } else {
                    throw new RuntimeException("Usuário está deslogado no banco. Acesso negado.");
                }

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Acesso negado: " + e.getMessage());
                return;
            }
        }

        // Continua a cadeia de filtros normalmente
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
