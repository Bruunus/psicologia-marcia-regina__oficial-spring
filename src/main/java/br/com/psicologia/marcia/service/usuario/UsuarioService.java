package br.com.psicologia.marcia.service.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.DTO.autenticacao.AccessUserManagerRecord;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.repository.usuario.UsuarioRepository;
import br.com.psicologia.marcia.security.TokenService;
import br.com.psicologia.marcia.security.TokenStore;
import br.com.psicologia.marcia.service.error.MessageError;

@Configuration
@Service
public class UsuarioService implements UserDetailsService {

	@Autowired
	private UsuarioRepository userRepository;
	
	@Autowired
    private AuditoriaDeSessaoDeUsuarioService auditoriaService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private MessageError messageError;
	
	private static Usuario usuario = new Usuario();

	private AccessUserManagerRecord login;
	
	 
	public UsuarioService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }
	 

 
	
	public boolean registrarUsuario(Usuario user) {
		
		if (userRepository.findBylogin(user.getUsername()) != null) {
            return true;
        } else {
        	
        	user.setSenha(passwordEncoder.encode(user.getPassword()));
        	user.setRole(user.getRole());
        	userRepository.save(user);
        	
        	return false;
        }
		
		
	}


	/**
	 * Login no sistema
	 */
	@Override
	public UserDetails loadUserByUsername(String username) /*throws UsernameNotFoundException*/ {
		
		System.out.println("LOGIN RECEBIDO: " +username);
		UsuarioService.usuario.setLogin(username);
		
		UserDetails findBylogin = userRepository.findBylogin(username);
		
		if(findBylogin == null) {
			System.out.println("Usuário inexistente (UserDetails)");
			messageError.setMessage("usuario_inexiste");
			throw new UsernameNotFoundException("Entrando em Exception -> HttpRequestMethodNotSupportedException: " + username);
		 
		} else {
			return findBylogin;
		}	
	}
	
	
	
	
	
	
	
	/**
	 * Verifica se este login fornecido é existente na tabela de usuários
	 * @param loginHttp
	 * @return
	 */
	public boolean validacaoDeLogin(String loginHttp) {
		System.out.println("Valor da pesquisa: "+loginHttp);
		String usuario = userRepository.procurarPorNome(loginHttp);
		if(usuario == null || usuario.equals("")) {
			return true;
		} else {
			return false;
		}
		
	}
	
	
	
	 

	/**
	 * Processa o logout do usuário a partir do token JWT fornecido.
	 *
	 * @param tokenJWT Token JWT extraído do cabeçalho Authorization.
	 * @return Mensagem de status indicando o resultado da operação.
	 */
	public String logout(String tokenJWT) {
	    try {
	        // Extrai o login do token
	        String login = tokenService.getSubject(tokenJWT);

	        // Verifica se o token é válido para o login fornecido
	        if (!TokenStore.tokenValido(login, tokenJWT)) {
	            return "Token inválido ou expirado.";
	        }

	        // Remove o token da memória
	        TokenStore.removerToken(login);
	        System.out.println("Token removido da memória para o usuário: " + login);

	        // Registra na auditoria
	        auditoriaService.registrarLogout(login, "LOGOUT_MANUAL");

	        return "Logout efetuado com sucesso.";

	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Erro ao processar logout: " + e.getMessage();
	    }
	}



	
	AccessUserManagerRecord conversorStringParaAccessUserManagerRecord(String login) {
        return new AccessUserManagerRecord(login);
    }
	

	 
	
	
	 
	

    
}
