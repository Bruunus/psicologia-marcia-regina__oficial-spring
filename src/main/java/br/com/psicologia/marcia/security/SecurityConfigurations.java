package br.com.psicologia.marcia.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.psicologia.marcia.repository.usuario.UsuarioRepository;
import br.com.psicologia.marcia.service.usuario.UsuarioService;

/**
 * Classe de configuração do Spring Security com autenticação JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
	
	private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    /**
     * Construtor com injeção de dependências necessárias para o filtro JWT.
     *
     * @param tokenService Serviço de token JWT
     * @param usuarioRepository Repositório de usuários
     */
    public SecurityConfigurations(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }
	
    
    
    /**
     * Configuração da cadeia de filtros de segurança e permissões das rotas.
     *
     * @param http Configuração HTTP do Spring Security
     * @return SecurityFilterChain Cadeia de filtros de segurança
     * @throws Exception
     */
	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, 
    		TokenService tokenService,
    		UsuarioService usuarioService) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.disable())
				.authorizeHttpRequests(auth -> auth
		                .requestMatchers(
		                		"/auth/login",
		                		"/auth/deslogar"
		                		).permitAll()
		                .anyRequest().authenticated()
				
//				Ativar para quando houver limpeza da tabela de usuarios para poder pelo menos criar um usuario ADMIN
//				.authorizeHttpRequests(auth -> auth
//					    .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
//					    .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
//					    .anyRequest().authenticated()
					

		            )
		            .addFilterBefore(new JwtAuthenticationFilter(tokenService, usuarioService),
		                    UsernamePasswordAuthenticationFilter.class)
		            .build();
				       
    }
	
	
	/**
     * Bean responsável por instanciar o AuthenticationManager padrão.
     *
     * @param configuration Configuração automática do Spring Security
     * @return AuthenticationManager
     * @throws Exception
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

	
    /**
     * Bean responsável por criptografar senhas usando o algoritmo BCrypt.
     *
     * @return PasswordEncoder Codificador de senhas
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	
}
