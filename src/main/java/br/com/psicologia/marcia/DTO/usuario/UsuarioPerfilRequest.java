package br.com.psicologia.marcia.DTO.usuario;

import jakarta.validation.constraints.NotBlank;

public class UsuarioPerfilRequest {

    @NotBlank(message = "perfil_obrigatorio")
    private String role;

    public String getRole() {
        return role;
    }
}