package br.com.psicologia.marcia.DTO.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RedefinirSenhaRequest {

    @NotBlank(message = "token_obrigatorio")
    private String token;

    @NotBlank(message = "senha_obrigatoria")
    @Size(min = 6, max = 100)
    private String senha;

    @NotBlank(message = "confirmacao_senha_obrigatoria")
    @Size(min = 6, max = 100)
    private String confirmacaoSenha;

    public String getToken() {
        return token;
    }

    public String getSenha() {
        return senha;
    }

    public String getConfirmacaoSenha() {
        return confirmacaoSenha;
    }
}