package br.com.psicologia.marcia.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade que representa o controle de auditoria de login e logout de um usuário.
 * Esta classe substitui o uso da entidade anterior 'GerenciadorDeAcessoDeUsuario'.
 */
@Entity
@Table(name = "auditoria_sessao_usuario")
public class AuditoriaDeSessaoDeUsuario {
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Login do usuário */
    @Column(nullable = false)
    private String login;

    /** Horário do login */
    @Column(name = "data_login", nullable = false)
    private LocalDateTime dataLogin;

    /** Horário do logout */
    @Column(name = "data_logout")
    private LocalDateTime dataLogout;

    /** IP de origem */
    @Column(name = "ip")
    private String ip;

    /** User-Agent do navegador ou dispositivo */
    @Column(name = "user_agent")
    private String userAgent;

    /** Motivo do logout (timeout, manual, etc) */
    @Column(name = "motivo_logout")
    private String motivoLogout;

    /** Status atual da sessão (ATIVA, ENCERRADA) */
    @Column(nullable = false)
    private String status;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public LocalDateTime getDataLogin() { return dataLogin; }
    public void setDataLogin(LocalDateTime dataLogin) { this.dataLogin = dataLogin; }

    public LocalDateTime getDataLogout() { return dataLogout; }
    public void setDataLogout(LocalDateTime dataLogout) { this.dataLogout = dataLogout; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getMotivoLogout() { return motivoLogout; }
    public void setMotivoLogout(String motivoLogout) { this.motivoLogout = motivoLogout; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
