package br.com.psicologia.marcia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.DadosTokenJWT;
import br.com.psicologia.marcia.JWT.TokenService;
import br.com.psicologia.marcia.model.DadosAutenticacao;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/")
public class ControllerLogin {

	@Autowired
	private AuthenticationManager manager;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UsuarioService userService;
	
 
	private Usuario usuario;
	
	
	
	
	@SuppressWarnings("rawtypes")
//	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@PostMapping("login")
	public ResponseEntity login(@Valid @RequestBody Usuario dados) {		 
		try {			
            var autenticacaoToken = new UsernamePasswordAuthenticationToken(dados.getLogin(), dados.getSenha());
            var autenticacao = manager.authenticate(autenticacaoToken);
            System.out.println(autenticacao);
            
            var tokenJWT =  tokenService.gerarToken((Usuario) autenticacao.getPrincipal());
            return ResponseEntity.ok(new DadosTokenJWT(tokenJWT)) ;
            
        } catch (Exception e) {
        	e.getMessage();
        	e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
		
	}
	
	 
	
	
	
	@PostMapping("register")
    public ResponseEntity<?> registerUser(@RequestBody Usuario user) {
		
		boolean registrarUsuario = userService.registrarUsuario(user);
		
		if(registrarUsuario) {
			return ResponseEntity.badRequest().body("Username already exists");
		} else {
			return ResponseEntity.ok("Usuário registrado com sucesso !!!");
		}
		
    }
	
}
