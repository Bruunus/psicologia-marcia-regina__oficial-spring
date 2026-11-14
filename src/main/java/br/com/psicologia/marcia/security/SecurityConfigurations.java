package br.com.psicologia.marcia.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import br.com.psicologia.marcia.repository.usuario.UsuarioRepository;
import br.com.psicologia.marcia.service.analytics.RequisicaoAnaliticaService;
import br.com.psicologia.marcia.service.analytics.SuporteAnalytics;
import br.com.psicologia.marcia.service.usuario.UsuarioService;

/**
 * Classe responsável pela configuração de segurança da aplicação.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfigurations {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    /**
     * Construtor da configuração de segurança.
     *
     * @param tokenService service de token JWT
     * @param usuarioRepository repositório de usuário
     */
    public SecurityConfigurations(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Define a cadeia de filtros de segurança da aplicação.
     *
     * @param http objeto de configuração HttpSecurity
     * @param tokenService service de token JWT
     * @param usuarioService service de usuário
     * @param requisicaoAnaliticaService service de analytics
     * @param suporteAnalytics suporte para analytics
     * @return SecurityFilterChain configurado
     * @throws Exception erro de configuração
     */
    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            TokenService tokenService,
            UsuarioService usuarioService,
            RequisicaoAnaliticaService requisicaoAnaliticaService,
            SuporteAnalytics suporteAnalytics,
            JwtCookieService jwtCookieService,
            @Value("${psicologia.marcia.auth.cookie-secure:false}")
            boolean cookieSecure) throws Exception {

        CookieCsrfTokenRepository csrfRepository =
                CookieCsrfTokenRepository.withHttpOnlyFalse();

        csrfRepository.setCookieCustomizer(cookie -> cookie
                .path("/")
                .secure(cookieSecure)
                .sameSite("Lax"));

        return http
                // Proteção CSRF para operações que alteram dados.
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfRepository)
                        .csrfTokenRequestHandler(
                                new SpaCsrfTokenRequestHandler()
                        ))

                // Mantido desabilitado por enquanto
                .cors(cors -> cors.disable())

                // Configura autorização das rotas
                .authorizeHttpRequests(auth -> auth

                        // Libera qualquer rota que NÃO comece com /api
                        .requestMatchers(
                                new RegexRequestMatcher("^/(?!api(?:/|$)).*$", null)
                        ).permitAll()

                        // Libera chamadas públicas específicas da API
                        .requestMatchers(
                                "/api/auth/csrf",
                                "/api/auth/login",
                                "/api/auth/deslogar",
                                "/api/edit/user/redefinir-senha",
                                "/api/edit/user/register",
                                "/api/usuarios/convites/validar-token",
                                "/api/usuarios/convites/definir-senha",
                                "/api/usuarios/redefinicoes-senha/validar-token",
                                "/api/usuarios/redefinicoes-senha/confirmar"
                        ).permitAll()

                        // Criacao de convites: somente administradores
                        .requestMatchers(
                                "/api/usuarios/convites"
                        ).hasAnyRole("ADMIN", "SUPER_ADMIN")

                        // Qualquer outra rota precisa de autenticação
                        .anyRequest().authenticated()
                )

                // Adiciona filtro JWT antes do filtro padrão de autenticação
                .addFilterBefore(
                        new JwtAuthenticationFilter(
                                tokenService,
                                usuarioService,
                                requisicaoAnaliticaService,
                                suporteAnalytics,
                                jwtCookieService
                        ),
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    /**
     * Expõe o AuthenticationManager do Spring.
     *
     * @param configuration configuração de autenticação
     * @return AuthenticationManager
     * @throws Exception erro ao obter manager
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Define o encoder de senha da aplicação.
     *
     * @return PasswordEncoder BCrypt
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}