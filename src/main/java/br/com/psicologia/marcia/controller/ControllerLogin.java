package br.com.psicologia.marcia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.AccessUserManagerRecord;
import br.com.psicologia.marcia.DTO.DadosTokenJWT;
import br.com.psicologia.marcia.JWT.TokenService;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.service.UsuarioService;
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
	
	
	
 
	
	
	 
	
	
	
	@SuppressWarnings("rawtypes")
//	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@PostMapping("login")	
	public ResponseEntity login(@Valid @RequestBody Usuario dados) {
			
			try {
//				System.out.println("Dados da request: Login: "+dados.getLogin()+"  Senha: "+ dados.getSenha());		//{Debug}\\
	            var autenticacaoToken = new UsernamePasswordAuthenticationToken(dados.getLogin(), dados.getSenha());
	            var autenticacao = manager.authenticate(autenticacaoToken);
//	            System.out.println(autenticacao);	 //{Debug}\\        
	       
	            if (autenticacao != null && autenticacao.isAuthenticated()) {
	            	
	            	
	            	boolean verificarAutenticacao = userService.verificarStatusAutenticacao(dados.getLogin());
	            	
	            	if(verificarAutenticacao) {
	        			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	        					.body("Já existe um usuário logado nesta conta, acesso negado.");
	        		} else {	            	
	            	
		                var tokenJWT = tokenService.gerarToken((Usuario) autenticacao.getPrincipal());
		                var nomeUsuario = ((Usuario) autenticacao.getPrincipal()).getLogin();
		                
		                return ResponseEntity.ok(new DadosTokenJWT(tokenJWT, nomeUsuario));
	        		}
	            } else {
	                // Se a autenticação falhar ou não estiver autenticada
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }
	            
	        } catch (Exception e) {
	        	e.getMessage();
	        	e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        }
		
		
		
		
		
	}
	

	
	
	@PostMapping("deslogar")
	public ResponseEntity<?> deslogar(@RequestBody AccessUserManagerRecord usuario) {
		String login = usuario.login();
		System.out.println("Usuario que chegou no controller: "+login);
		userService.deslogar(login);
		return ResponseEntity.ok("Usuário deslogado !");
	}
	
	
	
	
	
	@PostMapping("status-login")
	public ResponseEntity<?> statusDeAutenticacaoDeUsuario(@RequestBody AccessUserManagerRecord usuario) {
		// verificar no banco esse nome e busco pelo status do login e retorne bolean
		System.out.println("Valor do request: "+usuario);
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
	
	
	
	 
}
