package br.com.psicologia.marcia.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Paciente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nomeCompleto;
	
	private String cpf;
	
	private String email;
	
	private String telefone;
	
	private Short idade;
	
	private LocalDate dataNascimento;
	
	private String estadoCivil;
	
	private Short filhos;
	
	private String grauEscolaridade;
	
	private String profissao;
	
	private String endereco;
	
	private String queixa;
	
	@Enumerated(EnumType.STRING)
	private Perfil perfil;

	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	
	public String getNomeCompleto() {
		return nomeCompleto;
	}

	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	
	
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	

	public Short getIdade() {
		return idade;
	}

	public void setIdade(Short idade) {
		this.idade = idade;
	}
	
	

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
	

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}
	
	

	public Short getFilhos() {
		return filhos;
	}

	public void setFilhos(Short filhos) {
		this.filhos = filhos;
	}
	
	

	public String getGrauEscolaridade() {
		return grauEscolaridade;
	}

	public void setGrauEscolaridade(String grauEscolaridade) {
		this.grauEscolaridade = grauEscolaridade;
	}
	
	

	public String getProfissao() {
		return profissao;
	}

	public void setProfissao(String profissao) {
		this.profissao = profissao;
	}
	
	

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	
	

	public String getQueixa() {
		return queixa;
	}

	public void setQueixa(String queixa) {
		this.queixa = queixa;
	}
	
	
	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return 	this.id+" "+
				this.nomeCompleto+" "+
				this.cpf+" "+
				this.email+" "+
				this.telefone+" "+
				this.idade+" "+
				this.dataNascimento+" "+
				this.estadoCivil+" "+
				this.filhos+" "+
				this.grauEscolaridade+" "+
				this.profissao+" "+
				this.endereco+" "+
				this.queixa+" "+
				this.perfil+" ";
	}

	
 
	

}
