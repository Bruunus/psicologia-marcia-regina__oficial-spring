package br.com.psicologia.marcia.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {


	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/login").permitAll()
	                .requestMatchers("/register").permitAll()                
	                .anyRequest().authenticated()
	            )
	            .formLogin(form -> form
	                .loginPage("/login")
	                .permitAll()
	            )
	            .logout(logout -> logout
	                .permitAll()
	            ).csrf(csrf -> csrf.disable()).build();         
    }

	@Bean
	AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
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
	
	
	
}
