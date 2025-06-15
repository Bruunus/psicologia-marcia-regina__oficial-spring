package br.com.psicologia.marcia.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.repository.usuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Este filtro é executado uma vez por requisição e serve para interceptar e validar o token JWT
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    // Construtor que injeta os serviços necessários para validar o token e buscar o usuário
    public JwtAuthenticationFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    // Método principal que intercepta todas as requisições
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Extrai o token do cabeçalho Authorization
        String token = recuperarToken(request);

        if (token != null) {
            // Extrai o login (subject) de dentro do token
            String login = tokenService.getSubject(token);

            // Busca o usuário no banco de dados com base no login
            Usuario usuario = (Usuario) usuarioRepository.findBylogin(login);

            if (usuario != null) {
                // Cria um objeto de autenticação com as permissões do usuário
                var authentication = new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        usuario.getAuthorities()
                );

                // Define o usuário autenticado no contexto de segurança
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Segue o fluxo da requisição normalmente
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para recuperar o token do cabeçalho HTTP
    private String recuperarToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove o "Bearer " do início
        }
        return null;
    }
}
