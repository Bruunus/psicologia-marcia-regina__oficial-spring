package br.com.psicologia.marcia.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

//	Configuração de logout
//	@Autowired
//    @Lazy
//	private UsuarioService userService;
	
//	@Autowired
//	public void setUserService(UsuarioService userService) {
//        this.userService = userService;
//    }

	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.disable())
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers(HttpMethod.POST,"/login").permitAll()
	                .requestMatchers(HttpMethod.POST,"/register").permitAll() 
	                .requestMatchers(HttpMethod.POST,"/deslogar").permitAll()
	                .requestMatchers(HttpMethod.POST,"/status-login").permitAll() 
	                .requestMatchers(HttpMethod.POST,"/teste").permitAll()	// para testes
	                .requestMatchers(HttpMethod.GET,"/status-update").permitAll()
	                .anyRequest().authenticated()
                
//	            ).logout(logout -> logout
//	                    .logoutUrl("/logout")
//	                    .addLogoutHandler((request, response, authentication) -> {
//	                    	String login = request.getParameter("login");
//	                        System.out.println(login);
////	                        userService.deslogar(login);
//	                    })
//	                    .logoutSuccessHandler((request, response, authentication) -> {
//	                        response.setStatus(HttpServletResponse.SC_OK);
//	                    })
	            ).build();
				       
    }

	
	
//	Para o caso de verificar no banco as senhas encriptadas
	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	
//	Para o caso de não ter que codificar a senha, isso avisa o spring security
//	@Bean
//    PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }
	
	
	
//	@Bean
//	UrlBasedCorsConfigurationSource corsConfigurationSource() {
//	    CorsConfiguration configuration = new CorsConfiguration();
//	    configuration.setAllowedOrigins(Arrays.asList("*"));
//	    configuration.setAllowedMethods(Arrays.asList("GET", "POST","DELETE","UPDATE"));
//	    
//	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//	    source.registerCorsConfiguration("/**", configuration);
//	    
//	    return source;
//	}
	
	
	
	
}
