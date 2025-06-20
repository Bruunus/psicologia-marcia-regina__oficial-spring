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
import br.com.psicologia.marcia.service.usuario.AuditoriaDeSessaoDeUsuarioService;
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
	private AuditoriaDeSessaoDeUsuarioService auditoriaService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody Usuario dados) {
		
	    try {
	        var autenticacaoToken = new UsernamePasswordAuthenticationToken(dados.getLogin(), dados.getSenha());
	        var autenticacao = manager.authenticate(autenticacaoToken);
	        
	        var login = ((UserDetails) autenticacao.getPrincipal()).getUsername();
	        
	        if (TokenStore.usuarioJaLogado(login)) {
	        	messageErro.setMessage("usuario_ja_logado");
			    return ResponseEntity.status(HttpStatus.CONFLICT).body(messageErro.getMessage());
			} else {
				var token = tokenService.gerarToken(login);
		        TokenStore.adicionarToken(login, token);
		        var perfil = ((Usuario) autenticacao.getPrincipal()).getRole();
		        auditoriaService.registrarLogin(login);
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
	public ResponseEntity<String> logout(HttpServletRequest request) {
	    String authHeader = request.getHeader("Authorization");

	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido.");
	    }

	    String token = authHeader.replace("Bearer ", "");
	    String resultado = usuarioService.logout(token);

	    // Decide o status HTTP com base na mensagem retornada
	    if (resultado.contains("sucesso")) {
	        return ResponseEntity.ok(resultado);
	    } else if (resultado.contains("inválido") || resultado.contains("expirado")) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultado);
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultado);
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
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
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
