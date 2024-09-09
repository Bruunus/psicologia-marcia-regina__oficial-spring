package br.com.psicologia.marcia.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "gerenciador_de_acesso_de_usuario")
public class AccessUserManager {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	
	
	private String nome;
	
	private Boolean statusLogin;
	
	@Column(name = "ultimo_login")
	private LocalDateTime ultimo_login;
	
	@Column(name = "ultimo_logout")
	private LocalDateTime ultimo_logout;
	
	
	
	
//	@OneToOne
//	@JoinColumn(name = "usuario_id")
//	@JsonIgnore
//	private Usuario usuario;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Boolean getStatusLogin() {
		return statusLogin;
	}

	public void setStatusLogin(Boolean logado) {
		this.statusLogin = logado;
	}

	public LocalDateTime getUltimo_login() {
		return ultimo_login;
	}

	public void setUltimo_login(LocalDateTime ultimo_logout) {
		this.ultimo_login = ultimo_logout;
	}
	
	public LocalDateTime getUltimo_logout() {
		return ultimo_logout;
	}

	public void setUltimo_logout(LocalDateTime ultimo_logout) {
		this.ultimo_logout = ultimo_logout;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.nome+" "+this.statusLogin+" "+this.ultimo_login+" "+this.ultimo_logout;
	}
	
	
	

}
