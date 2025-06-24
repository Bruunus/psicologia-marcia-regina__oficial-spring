package br.com.psicologia.marcia.controller.usuario;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.usuario.UsuarioRedefinirSenha;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.service.usuario.UsuarioService;
import jakarta.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/edit/user")
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
    public ResponseEntity<?> deletarUsuario(@RequestBody Map<String, String> requestBody) {
        try {
            String login = requestBody.get("login");
            Long id = Long.valueOf(requestBody.get("id")); // Certifique-se de que o id venha também no JSON

            usuarioService.deletarUsuarioPorLoginEId(login, id);
            
            return ResponseEntity.ok(Collections.singletonMap("message", "Usuário deletado com sucesso"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Usuário não encontrado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erro ao deletar usuário"));
        }
    }
    
    
    
    
  @PostMapping("/redefinir-senha")
  //@RolesAllowed({"ADMIN", "SUPER_ADMIN"})
  public ResponseEntity<?> redefinirSenha(@RequestBody UsuarioRedefinirSenha request) {
      try {
          boolean senhaRedefinida = usuarioService.redefinirSenhaPorCpf(request);

          if (senhaRedefinida) {
              return ResponseEntity.ok(Collections.singletonMap("message", "Senha redefinida com sucesso"));
          } else {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                      .body(Collections.singletonMap("message", "Erro ao redefinir a senha. CPF inválido ou senhas não coincidem."));
          }

      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(Collections.singletonMap("message", "Erro ao redefinir a senha"));
      }
  }



}
