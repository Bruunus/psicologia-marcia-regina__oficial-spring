package br.com.psicologia.marcia.DTO.usuario;

public class ValidarTokenConviteResponse {

    private boolean valido;
    private String login;
    private String email;
    private String mensagem;

    public ValidarTokenConviteResponse(boolean valido, String login, String email, String mensagem) {
        this.valido = valido;
        this.login = login;
        this.email = email;
        this.mensagem = mensagem;
    }

    public boolean isValido() {
        return valido;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getMensagem() {
        return mensagem;
    }

}