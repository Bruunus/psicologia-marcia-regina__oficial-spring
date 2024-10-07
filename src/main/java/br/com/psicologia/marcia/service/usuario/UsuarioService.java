package br.com.psicologia.marcia.service.usuario;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.DTO.AccessUserManagerRecord;
import br.com.psicologia.marcia.model.AccessUserManager;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.repository.usuario.UserAccessRepository;
import br.com.psicologia.marcia.repository.usuario.UsuarioRepository;

@Configuration
@Service
public class UsuarioService implements UserDetailsService {

	@Autowired
	private UsuarioRepository userRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserAccessRepository userAccessRepository;
	
	 
	 

	
	public boolean registrarUsuario(Usuario user) {
		
		if (userRepository.findBylogin(user.getUsername()) != null) {
            return true;
        } else {
        	
        	user.setSenha(bCryptPasswordEncoder.encode(user.getPassword()));
        	userRepository.save(user);
        	
        	AccessUserManager usuarioGerenciavel = new AccessUserManager();
        	usuarioGerenciavel.setNome(user.getLogin());
        	usuarioGerenciavel.setStatusLogin(false);		// será atualizado
//        	usuarioGerenciavel.setTime_stamp(LocalDateTime.now());
        	userAccessRepository.save(usuarioGerenciavel);
        	
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
	
	
	public boolean verificarStatusAutenticacao(String usuarioLogin) {
		// procura o usuario no banco de dados com base na coluna status_login
		Boolean verificarUsuarioLogado = userAccessRepository.statusLoginUsuario(usuarioLogin);
		System.out.println("Retorno da query: "+ verificarUsuarioLogado);
		
		if(verificarUsuarioLogado) {	
			System.err.println("Ja existe um usuário logado");
			
			// se caso ja for true então retorna erro	
			return true;
		} else {
			// verifica se é false = 0 ---->>> se for então então pode atualizar pra 1 e retorna true 
			System.out.println("Esse usuário está logado agora !");			
			userAccessRepository.updateAcessoDeUsuario(
					usuarioLogin,
					true,
					LocalDateTime.now());
			
			return false;
		}
		
	}
	
	
	/**
	 * O objetivo principal dessa service é apenas verificar o status do login na base de dados
	 * @param login
	 * @return
	 */
	public boolean statusLogin(AccessUserManagerRecord loginHttp) {
		String login = loginHttp.login();
		Boolean statusLogin = userAccessRepository.statusLoginUsuario(login);
		if(statusLogin) {
			return true;
		} else {
			return false;
		}
		
	}


	public void deslogar(String usuarioRequest) {
		
		
//		String user = usuario.login();		 
		AccessUserManager findByNome = userAccessRepository.findByNome(usuarioRequest);
		String usuario = findByNome.getNome();
		userAccessRepository.updateLogoffDeUsuario(usuario, false, LocalDateTime.now());
		
		
		
//		System.out.println("Usário que vai se deslogar: "+usuario);
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
