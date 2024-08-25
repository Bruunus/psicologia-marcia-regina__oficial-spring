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
public class ControllerLogin {

	
	@Autowired
	private UsuarioService userService;
	
 
	private Usuario usuario;
	
	
	 
	
	
	
	@PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Usuario user) {
		
		boolean registrarUsuario = userService.registrarUsuario(user);
		
		if(registrarUsuario) {
			return ResponseEntity.badRequest().body("Username already exists");
		} else {
			return ResponseEntity.ok("Usuário registrado com sucesso !!!");
		}
		
    }
	
}
