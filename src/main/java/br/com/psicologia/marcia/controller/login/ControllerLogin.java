package br.com.psicologia.marcia.controller.login;

import java.util.HashMap;
import java.util.Map;

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
	public ResponseEntity<?> login(@Valid @RequestBody Usuario dados, HttpServletRequest request) {		
		
	    try {
	    	
	    	if (usuarioService.validacaoDeLogin(dados.getLogin())) {
	            messageErro.setMessage("usuario_inexistente");
	            System.err.println("Usuário inexistente: " + dados.getLogin());	            
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageErro.getMessage());
	        }
	    	
	        var autenticacaoToken = new UsernamePasswordAuthenticationToken(dados.getLogin(), dados.getSenha());
	        var autenticacao = manager.authenticate(autenticacaoToken);
	        
	        var login = ((UserDetails) autenticacao.getPrincipal()).getUsername();
	        
	        if (TokenStore.usuarioJaLogado(login)) {
	        	messageErro.setMessage("usuario_ja_logado");
	        	System.err.println("Este usuário já está logado");
			    return ResponseEntity.status(HttpStatus.CONFLICT).body(messageErro.getMessage());
			} else {
				var token = tokenService.gerarToken(login);
		        TokenStore.adicionarToken(login, token);
		        var perfil = ((Usuario) autenticacao.getPrincipal()).getRole();
		        auditoriaService.registrarLogin(login, request);
		        return ResponseEntity.ok(new DadosTokenJWT(token, login, perfil));
			}	        

	    } catch (BadCredentialsException e) {
	        messageErro.setMessage("senha_incorreta");
	        System.err.println(messageErro.getMessage());
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("senha_incorreta");

	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println("Erro inesperado de autenticação");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro de autenticação!");
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
	
	 
	
	 
}
