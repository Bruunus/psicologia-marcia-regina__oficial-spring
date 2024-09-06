package br.com.psicologia.marcia.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "gerenciador_de_acesso_de_usuario")
public class AccessUserManager {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	
	
	private String nome;
	
	private Boolean statusLogin;
	
	@Column(name = "timestamp")
	private LocalDateTime time_stamp;
	
	
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

	public LocalDateTime getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(LocalDateTime time_stamp) {
		this.time_stamp = time_stamp;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.nome+" "+this.statusLogin+" "+this.time_stamp;
	}
	
	
	

}
