package br.com.psicologia.marcia.controller.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.AccessUserManagerRecord;
import br.com.psicologia.marcia.DTO.DadosTokenJWT;
import br.com.psicologia.marcia.JWT.TokenService;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.service.error.MessageError;
import br.com.psicologia.marcia.service.usuario.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ControllerLogin {

	@Autowired
	private AuthenticationManager manager;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UsuarioService userService;
	
	@Autowired
	private MessageError messageErro;
	
	private static Usuario usuario = new Usuario();
	
	
	
	@PostMapping("login")
	public ResponseEntity<?> login(@Valid @RequestBody Usuario dados) {
	    ControllerLogin.usuario.setLogin(dados.getLogin());

	    // Verifique se o usuário existe antes de tentar autenticar
	    boolean usuarioNaoExiste = userService.validacaoDeLogin(dados.getLogin());
	    if (usuarioNaoExiste) {
	        messageErro.setMessage("usuario_inexistente");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	        		.body(messageErro.getMessage());
	    }

	    try {
	        var autenticacaoToken = new UsernamePasswordAuthenticationToken(dados.getLogin(), dados.getSenha());
	        var autenticacao = manager.authenticate(autenticacaoToken);

	        if (autenticacao != null && autenticacao.isAuthenticated()) {
	            boolean verificarAutenticacao = userService.verificarStatusAutenticacao(dados.getLogin());

	            if (verificarAutenticacao) {
	                messageErro.setMessage("usuario_ja_logado");
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageErro.getMessage());
	            } else {
	                var tokenJWT = tokenService.gerarToken((Usuario) autenticacao.getPrincipal());
	                var nomeUsuario = ((Usuario) autenticacao.getPrincipal()).getLogin();
	                return ResponseEntity.ok(new DadosTokenJWT(tokenJWT, nomeUsuario));
	            }
	        } else {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        }

	    } catch (BadCredentialsException e) {
	        // Captura a exceção de credenciais inválidas (senha incorreta)
	        messageErro.setMessage("senha_incorreta");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageErro.getMessage());

	    } catch (Exception e) {
	        // Captura qualquer outra exceção
	        e.printStackTrace(); // Log da exceção
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor");
	    }
	}
	

	
	
	@PostMapping("deslogar")
	public ResponseEntity<?> deslogar(@RequestBody AccessUserManagerRecord usuario) {
		String login = usuario.login();
		System.out.println("Usuario que chegou no controller: "+login);
		userService.deslogar(login);
		return ResponseEntity.ok("{\"message\": \"Usuário deslogado !\"}");
	}
	
	
	
	
	
	@PostMapping("status-login")
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
	
	@GetMapping("status-update")
	public ResponseEntity<?> verificacaoDeStatusPollingDeUsuario() {
	 
		boolean statusLogin = userService.statusUpdateService();
		if(statusLogin) {			 
			return ResponseEntity.ok(statusLogin);
		} else {
			return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
		}
			 
	}
	
	 
	
	
	
	@PostMapping("register")
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
