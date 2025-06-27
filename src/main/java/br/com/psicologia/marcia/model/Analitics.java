package br.com.psicologia.marcia.model;

import java.time.LocalDateTime;

import br.com.psicologia.marcia.model.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade que representa uma requisição realizada ao sistema, 
 * com objetivo de rastreamento e análise de uso (analytics).
 */
@Entity
@Table(name = "analitics")
public class Analitics {

   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String endpoint;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = true)
    private String user;

    @Column(nullable = false)
    private Integer httpStatusReturn;
    
    @Column(nullable = false)
    private Long duration;

    @Column(length = 255)
    private String userAgent;

    @Column(length = 255)
    private String respostaCurta;
    
    @Enumerated(EnumType.STRING)
	private Role role;
    
    private String objective; // Novo campo
    
    

    
    
    
    // Getters e Setters

    public Long getId() {
        return id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMetodo() {
        return method;
    }

    public void setMetodo(String method) {
        this.method = method;
    }

    public LocalDateTime getDataHora() {
        return timestamp;
    }

    public void setDataHora(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsuario() {
        return user;
    }

    public void setUsuario(String user) {
        this.user = user;
    }

    public Integer getStatusHttp() {
        return httpStatusReturn;
    }

    public void setStatusHttp(Integer httpStatusReturn) {
        this.httpStatusReturn = httpStatusReturn;
    }

    public Long getDuracao() {
        return duration;
    }

    public void setDuracao(Long duration) {
        this.duration = duration;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getRespostaCurta() {
        return respostaCurta;
    }

    public void setRespostaCurta(String respostaCurta) {
        this.respostaCurta = respostaCurta;
    }

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}
