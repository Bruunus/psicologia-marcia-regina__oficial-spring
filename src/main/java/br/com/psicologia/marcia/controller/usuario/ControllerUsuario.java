package br.com.psicologia.marcia.controller.usuario;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.usuario.UsuarioStatusRequest;
import br.com.psicologia.marcia.error.ApiErrorResponse;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.model.enums.StatusUsuario;
import br.com.psicologia.marcia.repository.usuario.UsuarioRepository;
import br.com.psicologia.marcia.service.usuario.UsuarioAdministracaoService;

@RestController
@RequestMapping("/api/edit/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ControllerUsuario {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioAdministracaoService usuarioAdministracaoService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Usuario user) {
        return erro(
                HttpStatus.GONE,
                "cadastro_direto_desativado_utilize_convite",
                "O cadastro direto está desativado. Utilize o convite de acesso."
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/deletar")
    public ResponseEntity<?> deletarUsuario(
            @RequestBody Map<String, String> requestBody,
            Authentication authentication
    ) {

        String login = requestBody != null
                ? requestBody.get("login")
                : null;

        String idInformado = requestBody != null
                ? requestBody.get("id")
                : null;

        if (login == null || login.isBlank()
                || idInformado == null || idInformado.isBlank()) {

            return erro(
                    HttpStatus.BAD_REQUEST,
                    "login_e_id_obrigatorios",
                    "Informe o login e o ID do usuário."
            );
        }

        Long id;

        try {
            id = Long.valueOf(idInformado);
        } catch (NumberFormatException e) {

            return erro(
                    HttpStatus.BAD_REQUEST,
                    "id_usuario_invalido",
                    "O ID do usuário informado é inválido."
            );
        }

        if (usuarioRepository.findByLoginAndId(login, id).isEmpty()) {

            return erro(
                    HttpStatus.NOT_FOUND,
                    "usuario_nao_encontrado",
                    "Usuário não encontrado."
            );
        }

        UsuarioStatusRequest dados = new UsuarioStatusRequest();
        dados.setStatusUsuario(StatusUsuario.INATIVO);

        // As exceções de negócio são tratadas pelo
        // GlobalApiExceptionHandler.
        usuarioAdministracaoService.atualizarStatus(
                id,
                dados,
                authentication.getName()
        );

        return ResponseEntity.ok(
                Collections.singletonMap(
                        "message",
                        "Usuário inativado com sucesso"
                )
        );
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha() {

        return erro(
                HttpStatus.GONE,
                "redefinicao_por_cpf_desativada_utilize_recuperacao_por_email",
                "A redefinição por CPF está desativada. Utilize a recuperação por e-mail."
        );
    }

    private ResponseEntity<ApiErrorResponse> erro(
            HttpStatus status,
            String codigo,
            String mensagem
    ) {

        return ResponseEntity
                .status(status)
                .body(
                        ApiErrorResponse.criar(
                                status,
                                codigo,
                                mensagem
                        )
                );
    }
}
