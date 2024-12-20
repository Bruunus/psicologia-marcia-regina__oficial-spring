package br.com.psicologia.marcia.model;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "paciente")
public class Paciente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nomeCompleto;
	
	private String cpf;
	
	private String email;
	
	private String telefone;
	
	private String telefoneContato;
	
	private Short idade;
	
	private LocalDate dataNascimento;
	
	private String estadoCivil;
	
	private Boolean filhos;
	
	private Short qtdFilhos;
	
	private String grauEscolaridade;
	
	private String profissao;
	
	@Enumerated(EnumType.STRING)
	private Perfil perfil;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
	private Endereco endereco;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "queixa_id", referencedColumnName = "id")
	private Queixa queixa;
	
	@Enumerated(EnumType.STRING)
	private StatusPaciente statusPaciente;
	
	
	
	

	
	
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
	
	

	public Boolean getFilhos() {
		return filhos;
	}

	public void setFilhos(Boolean filhos) {
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
	
	

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}
	
	

	public Queixa getQueixa() {
		return queixa;
	}

	public void setQueixa(Queixa queixa) {
		this.queixa = queixa;
	}
	
	
	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	
	public StatusPaciente getStatusPaciente() {
		return statusPaciente;
	}

	public void setStatusPaciente(StatusPaciente statusPaciente) {
		this.statusPaciente = statusPaciente;
	}
	
	public String getTelefoneContato() {
		return telefoneContato;
	}

	public void setTelefoneContato(String telefoneContato) {
		this.telefoneContato = telefoneContato;
	}

	public Short getQtdFilhos() {
		return qtdFilhos;
	}

	public void setQtdFilhos(Short qtdFilhos) {
		this.qtdFilhos = qtdFilhos;
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
				this.queixa+" "+
				this.perfil+" ";
	}

	

	

	 

	
 
	

}
