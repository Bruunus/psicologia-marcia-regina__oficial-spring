package br.com.psicologia.marcia.DTO.usuario;

import java.time.LocalDateTime;

public class SolicitarRedefinicaoSenhaResponse {

    private String mensagem;
    private String login;
    private String email;
    private LocalDateTime expiraEm;
    private String linkRedefinicaoDev;

    public SolicitarRedefinicaoSenhaResponse(
        String mensagem,
        String login,
        String email,
        LocalDateTime expiraEm,
        String linkRedefinicaoDev
    ) {
        this.mensagem = mensagem;
        this.login = login;
        this.email = email;
        this.expiraEm = expiraEm;
        this.linkRedefinicaoDev = linkRedefinicaoDev;
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

    public LocalDateTime getExpiraEm() {
        return expiraEm;
    }

    public String getLinkRedefinicaoDev() {
        return linkRedefinicaoDev;
    }
}