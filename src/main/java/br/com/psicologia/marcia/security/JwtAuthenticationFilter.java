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

/**
 * Filtro JWT que intercepta todas as requisições HTTP para validar o token JWT,
 * autenticar o usuário e verificar se ele ainda está com sessão ativa.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final GerenciadorDeAcessoDeUsuarioRepository acessoRepo;

    /**
     * Construtor para injeção de dependências.
     */
    public JwtAuthenticationFilter(TokenService tokenService,
                                   UsuarioService usuarioService,
                                   GerenciadorDeAcessoDeUsuarioRepository acessoRepo) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
        this.acessoRepo = acessoRepo;
    }

    /**
     * Método que intercepta todas as requisições e aplica a lógica de autenticação via JWT.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Extrai o token do cabeçalho Authorization
        String token = recuperarToken(request);

        // Verifica se o token está presente e válido
        if (token != null && !token.isBlank()) {
            try {
                // Extrai o login (username) do token
                String login = tokenService.getSubject(token);

                // Verifica no banco se o usuário está logado (status_login = 1)
                boolean usuarioLogado = acessoRepo.existsByNomeAndStatusLogin(login, true);

                if (usuarioLogado) {
                    // Carrega os dados do usuário
                    UserDetails userDetails = usuarioService.loadUserByUsername(login);

                    // Cria a autenticação e insere no contexto de segurança
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // Log opcional
                    System.out.println("Usuário autenticado via filtro: " + login + " | Token: " + token);

                } else {
                    throw new RuntimeException("Usuário deslogado no banco. Acesso negado.");
                }

            } catch (Exception e) {
                throw new RuntimeException("Token JWT inválido ou expirado!", e);
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
