package br.com.psicologia.marcia.DTO.usuario;

import java.time.LocalDateTime;

public class UsuarioConviteResponse {

    private String mensagem;
    private String login;
    private String email;
    private String role;
    private LocalDateTime expiraEm;
    private String linkConviteDev;

    public UsuarioConviteResponse(
            String mensagem,
            String login,
            String email,
            String role,
            LocalDateTime expiraEm,
            String linkConviteDev
    ) {
        this.mensagem = mensagem;
        this.login = login;
        this.email = email;
        this.role = role;
        this.expiraEm = expiraEm;
        this.linkConviteDev = linkConviteDev;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getExpiraEm() {
        return expiraEm;
    }

    public String getLinkConviteDev() {
        return linkConviteDev;
    }

}
