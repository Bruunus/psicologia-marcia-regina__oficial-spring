package br.com.psicologia.marcia.controller.usuario;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.usuario.DefinirSenhaConviteRequest;
import br.com.psicologia.marcia.DTO.usuario.UsuarioConviteRequest;
import br.com.psicologia.marcia.DTO.usuario.UsuarioConviteResponse;
import br.com.psicologia.marcia.DTO.usuario.ValidarTokenConviteResponse;
import br.com.psicologia.marcia.service.usuario.UsuarioConviteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
public class UsuarioConviteController {

    private final UsuarioConviteService usuarioConviteService;

    public UsuarioConviteController(UsuarioConviteService usuarioConviteService) {
        this.usuarioConviteService = usuarioConviteService;
    }

    @PostMapping("/api/usuarios/convites")
    public ResponseEntity<UsuarioConviteResponse> criarConvite(
            @Valid @RequestBody UsuarioConviteRequest request
    ) {
        UsuarioConviteResponse response = usuarioConviteService.criarConvite(request);

        return ResponseEntity.ok(response);
    }

    public record ValidarTokenRequest(@NotBlank String token) {}

    @PostMapping("/api/usuarios/convites/validar-token")
    public ResponseEntity<ValidarTokenConviteResponse> validarToken(
            @Valid @RequestBody ValidarTokenRequest request
    ) {
        ValidarTokenConviteResponse response =
                usuarioConviteService.validarToken(request.token());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/usuarios/convites/definir-senha")
    public ResponseEntity<String> definirSenha(
            @Valid @RequestBody DefinirSenhaConviteRequest request
    ) {
        usuarioConviteService.definirSenha(request);

        return ResponseEntity.ok("senha_cadastrada_com_sucesso");
    }


}