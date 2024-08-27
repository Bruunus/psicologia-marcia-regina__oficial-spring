package br.com.psicologia.marcia.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.repository.UsuarioRepository;

@Configuration
@Service
public class UsuarioService implements UserDetailsService {

	@Autowired
	private UsuarioRepository userRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	 
	 

	
	public boolean registrarUsuario(Usuario user) {
		
		if (userRepository.findBylogin(user.getUsername()) != null) {
            return true;
        } else {
        	
        	user.setSenha(bCryptPasswordEncoder.encode(user.getPassword()));
        	userRepository.save(user);
        	return false;
        }
		
		
	}


	/**
	 * Login no sistema
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		System.out.println("LOGIN RECEBIDO: " +username);
		
		UserDetails findBylogin = userRepository.findBylogin(username);
		
		if(findBylogin == null) {
			System.out.println("O valor está nulo");
			throw new UsernameNotFoundException("Entrando em Exception -> HttpRequestMethodNotSupportedException: " + username);
			 
		} else {
			return findBylogin;
		}
		
		

		
	}
	
	
	@Bean
	AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
	
	
//	@Bean
//	BCryptPasswordEncoder bCryptPasswordEncoder() {
//	    return new BCryptPasswordEncoder();
//	}
    
	 
	
	
	 
	
	 
    
	
//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//	    Usuario user = userRepository.findBylogin(username)
//	            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//	    return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), 
//	            Arrays.asList(new SimpleGrantedAuthority(user.getRole())));
//	}
    
    
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
    
}
