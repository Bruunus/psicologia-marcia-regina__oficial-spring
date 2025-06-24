package br.com.psicologia.marcia.service.usuario;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.DTO.autenticacao.AccessUserManagerRecord;
import br.com.psicologia.marcia.DTO.usuario.UsuarioRedefinirSenha;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.repository.usuario.UsuarioRepository;
import br.com.psicologia.marcia.security.TokenService;
import br.com.psicologia.marcia.security.TokenStore;
import br.com.psicologia.marcia.service.error.MessageError;

@Configuration
@Service
public class UsuarioService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
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
        this.usuarioRepository = userRepository;
    }
	 

 
	/**
	 * Registra um novo usuário no sistema.
	 * 
	 * - Verifica se o login já está cadastrado.
	 * - Criptografa o CPF e a senha antes de persistir os dados.
	 * - Salva o novo usuário com as informações seguras no banco.
	 *
	 * @param user Objeto do tipo {@link Usuario} contendo os dados do novo usuário a ser registrado.
	 * @return {@code true} se o login já existe (registro não efetuado), {@code false} se o registro foi realizado com sucesso.
	 */

	public boolean registrarUsuario(Usuario user) {

	    if (usuarioRepository.findBylogin(user.getUsername()) != null) {
	        return true;
	    }
 
	    if (user.getCpf() == null || user.getCpf().length() != 11) {
	        throw new IllegalArgumentException("O CPF inválico para cadastro - verifique o valor.");
	    }

	    // Criptografar CPF e senha
	    String cpfCriptografado = passwordEncoder.encode(user.getCpf());
	    user.setCpf(cpfCriptografado);
	    user.setSenha(passwordEncoder.encode(user.getPassword()));

	    usuarioRepository.save(user);
	    return false;
	}

	
	
	/**
	 * Redefine a senha de um usuário com base no CPF informado.
	 * 
	 * - Percorre todos os usuários do sistema.
	 * - Compara o CPF fornecido com os CPF's criptografados salvos no banco usando {@code passwordEncoder.matches}.
	 * - Caso o CPF seja válido e as novas senhas coincidam, atualiza a senha do usuário.
	 *
	 * @param request Objeto do tipo {@link UsuarioRedefinirSenha} contendo CPF, nova senha e confirmação da senha.
	 * @return {@code true} se a senha foi redefinida com sucesso, {@code false} se o CPF não foi encontrado ou as senhas não coincidem.
	 */

	public boolean redefinirSenhaPorCpf(UsuarioRedefinirSenha request) {
	    List<Usuario> usuarios = usuarioRepository.findAll();

	    Optional<Usuario> optionalUsuario = usuarios.stream()
	        .filter(u -> passwordEncoder.matches(request.cpf(), u.getCpf()))
	        .findFirst();

	    if (optionalUsuario.isEmpty()) {
	        return false;
	    }

	    if (!request.novaSenha().equals(request.confirmacaoSenha())) {
	        return false;
	    }

	    Usuario usuario = optionalUsuario.get();
	    usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
	    usuarioRepository.save(usuario);
	    return true;
	}
	
	
	
	



	/**
	 * Carrega os detalhes de um usuário com base no nome de usuário (login) fornecido.
	 * 
	 * - Registra o login recebido no log.
	 * - Armazena temporariamente o login recebido na variável estática {@code UsuarioService.usuario}.
	 * - Busca o usuário correspondente no repositório.
	 * - Caso o usuário não seja encontrado, lança uma {@link UsernameNotFoundException} com uma mensagem descritiva.
	 * - Se encontrado, retorna um objeto {@link UserDetails} com as credenciais do usuário.
	 *
	 * @param username O login do usuário a ser buscado.
	 * @return Um objeto {@link UserDetails} contendo os dados do usuário autenticado.
	 * @throws UsernameNotFoundException Se o usuário não for encontrado no banco de dados.
	 */

	@Override
	public UserDetails loadUserByUsername(String username) /*throws UsernameNotFoundException*/ {
		
		System.out.println("LOGIN RECEBIDO: " +username);
		UsuarioService.usuario.setLogin(username);
		
		UserDetails findBylogin = usuarioRepository.findBylogin(username);
		
		if(findBylogin == null) {
			System.out.println("Usuário inexistente (UserDetails)");
			messageError.setMessage("usuario_inexiste");
			throw new UsernameNotFoundException("Entrando em Exception -> HttpRequestMethodNotSupportedException: " + username);
		 
		} else {
			return findBylogin;
		}	
	}
	
	
	
	
	/**
	 * Deleta um usuário com base no login e id fornecidos.
	 *
	 * @param login O login do usuário a ser deletado.
	 * @param id    O ID do usuário a ser deletado.
	 * @throws NoSuchElementException se o usuário não for encontrado.
	 */
	public void deletarUsuarioPorLoginEId(String login, Long id) {
	    Optional<Usuario> usuarioOptional = usuarioRepository.findByLoginAndId(login, id);

	    if (usuarioOptional.isPresent()) {
	        usuarioRepository.delete(usuarioOptional.get());
	    } else {
	        throw new NoSuchElementException("Usuário não encontrado com login: " + login + " e id: " + id);
	    }
	}

	
	
	
	/**
	 * Verifica se existe um usuário com o login informado.
	 *
	 * @param login o login do usuário a ser verificado
	 * @return true se o usuário NÃO existe, false se existe
	 */
	public boolean validacaoDeLogin(String login) {
	    return !usuarioRepository.existsByLogin(login);
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




	/**
     * Exclui um usuário com base no login e ID fornecidos.
     *
     * @param login Login do usuário.
     * @param id    ID do usuário.
     * @return true se o usuário foi excluído com sucesso, false se não foi encontrado.
     */
    public boolean deletarUsuario(String login, Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByIdAndLogin(id, login);

        if (usuarioOptional.isPresent()) {
            usuarioRepository.delete(usuarioOptional.get());
            return true;
        }

        return false;
    }
	

	 
	
	
	 
	

    
}
