package br.com.psicologia.marcia.service.usuario;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.DTO.autenticacao.AccessUserManagerRecord;
import br.com.psicologia.marcia.model.GerenciadorDeAcessoDeUsuario;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.repository.usuario.GerenciadorDeAcessoDeUsuarioRepository;
import br.com.psicologia.marcia.repository.usuario.UsuarioRepository;
import br.com.psicologia.marcia.service.error.MessageError;

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
	private GerenciadorDeAcessoDeUsuarioRepository gerenciadorDeAcessoDeUsuarioRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepositoty;
	
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
        	
        	user.setSenha(bCryptPasswordEncoder.encode(user.getPassword()));
        	userRepository.save(user);
        	
        	GerenciadorDeAcessoDeUsuario usuarioGerenciavel = new GerenciadorDeAcessoDeUsuario();
        	usuarioGerenciavel.setNome(user.getLogin());
        	usuarioGerenciavel.setStatusLogin(false);		// será atualizado
//        	usuarioGerenciavel.setTime_stamp(LocalDateTime.now());
        	gerenciadorDeAcessoDeUsuarioRepository.save(usuarioGerenciavel);
        	
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
	
	
	
	
	
	public boolean verificarStatusAutenticacao(String usuarioLogin) {
		// procura o usuario no banco de dados com base na coluna status_login
		Boolean verificarUsuarioLogado = gerenciadorDeAcessoDeUsuarioRepository.statusLoginUsuario(usuarioLogin);
		System.out.println("Retorno da query: "+ verificarUsuarioLogado);
		
		if(verificarUsuarioLogado) {
			messageError.setMessage("usuario_ja_logado");
			return true;
			
			
			
		} else {
			// verifica se é false = 0 ---->>> se for então então pode atualizar pra 1 e retorna true 
			System.out.println("Esse usuário está logado agora !");			
			gerenciadorDeAcessoDeUsuarioRepository.updateAcessoDeUsuario(
					usuarioLogin,
					true,
					LocalDateTime.now());
			
			return false;
		}
		
	}
	
	/**
	 * Verifica se este login fornecido é existente na tabela de usuários
	 * @param loginHttp
	 * @return
	 */
	public boolean validacaoDeLogin(String loginHttp) {
		System.out.println("Valor da pesquisa: "+loginHttp);
		String usuario = gerenciadorDeAcessoDeUsuarioRepository.procurarPorNome(loginHttp);
		if(usuario == null || usuario.equals("")) {
			return true;
		} else {
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
		Boolean statusLogin = gerenciadorDeAcessoDeUsuarioRepository.statusLoginUsuario(login);
		if(statusLogin) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * O objetivo principal dessa service é atualizar o status do login para false validando e 
	 * certificando o logoff do usuário 
	 * 
	 * @param login
	 * @return
	 */
	public boolean statusUpdateService() {		
		
		String username = UsuarioService.usuario.getLogin();
		System.out.println("Usuário a se deslogar: "+username);
		deslogar(username);	
		
		GerenciadorDeAcessoDeUsuario findByNome = gerenciadorDeAcessoDeUsuarioRepository.findByNome(username);
		Boolean statusLogin = findByNome.getStatusLogin();
		
		System.out.println("Status desse usuário após deslogar: "+statusLogin);
		if(statusLogin) {
			System.out.println("Usuário deslogado");
			return true;
		} else {
			throw new RuntimeException("Erro ao atualizar status e deslogar usuário");
		}		
		
	}
	
	 

	/**
	 * Metodo para deslogar um usuário da sessão 
	 * @param usuarioRequest
	 */
	public void deslogar(String usuarioRequest) {		
//		String user = usuario.login();		 
		GerenciadorDeAcessoDeUsuario findByNome = gerenciadorDeAcessoDeUsuarioRepository.findByNome(usuarioRequest);
		String usuario = findByNome.getNome();
		gerenciadorDeAcessoDeUsuarioRepository.updateLogoffDeUsuario(usuario, false, LocalDateTime.now());		
//		System.out.println("Usário que vai se deslogar: "+usuario);
	}
	
	AccessUserManagerRecord conversorStringParaAccessUserManagerRecord(String login) {
        return new AccessUserManagerRecord(login);
    }
	

	 
	
	
	 
	

    
}
