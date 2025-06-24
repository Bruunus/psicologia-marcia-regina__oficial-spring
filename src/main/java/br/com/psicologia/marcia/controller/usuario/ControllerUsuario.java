package br.com.psicologia.marcia.controller.usuario;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.service.usuario.UsuarioService;
import jakarta.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/auth/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ControllerUsuario {

    @Autowired
    private UsuarioService usuarioService;
    
    
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Usuario user) {
		
		System.out.println(user);
		
		boolean registrarUsuario = usuarioService.registrarUsuario(user);
		
		if(registrarUsuario) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
		} else {
			return ResponseEntity.ok("Usuário registrado com sucesso !!!");
		}
		
    }
    
    

    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    @DeleteMapping("/deletar")
    public ResponseEntity<?> deletarUsuario(@RequestParam String login, @RequestParam Long id) {
        try {
            boolean deletado = usuarioService.deletarUsuario(login, id);
            if (deletado) {
                return ResponseEntity.ok(Collections.singletonMap("message", "Usuário excluído com sucesso"));
            } else {
                return ResponseEntity.status(404).body(Collections.singletonMap("message", "Usuário não encontrado"));
            }
        } catch (Exception e) {
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
            return ResponseEntity.status(500).body(Collections.singletonMap("message", "Erro interno ao excluir usuário"));
        }
    }
}
