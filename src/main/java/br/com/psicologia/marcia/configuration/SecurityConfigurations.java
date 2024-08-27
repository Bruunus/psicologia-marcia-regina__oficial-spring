package br.com.psicologia.marcia.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {


	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.disable())
	            .authorizeHttpRequests(auth -> auth
//	                .requestMatchers("/login").permitAll()
//	                .requestMatchers("/register").permitAll()                
	                .anyRequest().authenticated()
	            )
//	            .formLogin(form -> form
//	                .loginPage("/login")
//	                .permitAll()
//	            )
//	            .logout(logout -> logout
//	                .permitAll()
//	            )
	            
	            .build();
				       
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
//	CorsConfigurationSource corsConfigurationSource() {
//		CorsConfiguration configuration = new CorsConfiguration();
//		configuration.setAllowedOrigins(Arrays.asList("*"));
//		configuration.setAllowedMethods(Arrays.asList("GET","POST"));
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/**", configuration);
//		return (CorsConfigurationSource) source;
//	}
	
	
	
	
}
