package br.com.psicologia.marcia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	        			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
	
	
	@PostMapping("logout")
	public ResponseEntity<?> deslogarUsuario(@RequestBody String user) {
		// verificar se esse nome da string existe no banco de dados
		
		// faz a chamada na service para deslogar o usuário fazendo update para "0 = false"
		
		return ResponseEntity.ok("Usuário deslogado!");
	}
	
	
	
	
	
	@PostMapping("status-login")
	public boolean statusDeAutenticacaoDeUsuario(@RequestBody String usuario) {
		// verificar no banco esse nome e busco pelo status do login e retorne bolean
		
		// faz a chamada na service para deslogar o usuário fazendo update para "0 = false"
		
		return true;
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
