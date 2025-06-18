package br.com.psicologia.marcia.controller.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.autenticacao.AccessUserManagerRecord;
import br.com.psicologia.marcia.DTO.autenticacao.DadosTokenJWT;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.security.TokenService;
import br.com.psicologia.marcia.security.TokenStore;
import br.com.psicologia.marcia.service.error.MessageError;
import br.com.psicologia.marcia.service.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
//@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ControllerLogin {

	@Autowired
	private AuthenticationManager manager;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UsuarioService userService;
	
	@Autowired
	private MessageError messageErro;
	
	@Autowired
	private TokenStore tokenStore;
	
	
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody Usuario dados) {
		
	    try {
	        var autenticacaoToken = new UsernamePasswordAuthenticationToken(dados.getLogin(), dados.getSenha());
	        var autenticacao = manager.authenticate(autenticacaoToken);
	        
	        var login = ((UserDetails) autenticacao.getPrincipal()).getUsername();
	        
	        if (TokenStore.usuarioJaLogado(login)) {
			    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("O usuário já está logado.");
			} else {
				var token = tokenService.gerarToken(login);
		        TokenStore.adicionarToken(login, token);
		        var perfil = ((Usuario) autenticacao.getPrincipal()).getRole();
		        return ResponseEntity.ok(new DadosTokenJWT(token, login, perfil));
			}	        

	    } catch (BadCredentialsException e) {
	        messageErro.setMessage("senha_incorreta");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Senha incorreta !\"}");

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Erro de autenticação!\"}");
	    }
	}

	

	/**
	 * Endpoint para realizar o logout do usuário.
	 * Remove o token JWT da memória (TokenStore), invalidando a sessão.
	 *
	 * @param request Requisição HTTP contendo o token no cabeçalho Authorization.
	 * @return Mensagem de sucesso ou erro.
	 */
	@PostMapping("/deslogar")
	public ResponseEntity<?> logout(HttpServletRequest request) {
	    String token = tokenService.recuperarToken(request);

	    if (token == null || token.isBlank()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido.");
	    }

	    try {
	        String login = tokenService.getSubject(token);

	        if (!TokenStore.tokenValido(login, token)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado.");
	        }

	        TokenStore.removerToken(login);
	        System.out.println("Token removido da memória para o usuário: " + login);

	        return ResponseEntity.ok("Logout efetuado com sucesso.");

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro ao processar logout: " + e.getMessage());
	    }
	}
	


	
	
	
	
	
	@PostMapping("/status-login")
	public ResponseEntity<?> statusDeAutenticacaoDeUsuario(@RequestBody AccessUserManagerRecord usuario) {
		// verificar no banco esse nome e busco pelo status do login e retorne bolean
		System.out.println("Entrado em 'status logins => Valor do request: "+usuario);
		boolean statusLogin = userService.statusLogin(usuario);
		if(statusLogin) {
			System.err.println("O usuário já está logado");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Já existe um usuário logado nesta conta, acesso negado.");
		} else {
			System.out.println("Usuário não está logado");
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		 
	}
	
	@GetMapping("/status-update")
	public ResponseEntity<?> verificacaoDeStatusPollingDeUsuario() {
	 
		boolean statusLogin = userService.statusUpdateService();
		if(statusLogin) {			 
			return ResponseEntity.ok(statusLogin);
		} else {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
		}
			 
	}
	
	 
	
	
	
	@PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Usuario user) {
		
		System.out.println(user);
		
		boolean registrarUsuario = userService.registrarUsuario(user);
		
		if(registrarUsuario) {
			return ResponseEntity.badRequest().body("Username already exists");
		} else {
			return ResponseEntity.ok("Usuário registrado com sucesso !!!");
		}
		
    }
	
	
//	@PostMapping("teste")
//    public ResponseEntity<?> testUser(@RequestBody Usuario user) {
//		String login = user.getLogin();
//		boolean validarExistenciaDeUsuario = userService.validacaoDeLogin(login);
//		System.out.println(validarExistenciaDeUsuario);
//    	
//		return ResponseEntity.ok("Teste");
//	}
	
	
	 
}
