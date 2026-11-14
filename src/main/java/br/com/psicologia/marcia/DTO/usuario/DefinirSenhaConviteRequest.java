package br.com.psicologia.marcia.DTO.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DefinirSenhaConviteRequest {

    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 6, max = 100)
    private String senha;

    public String getToken() {
        return token;
    }

    public String getSenha() {
        return senha;
    }

}