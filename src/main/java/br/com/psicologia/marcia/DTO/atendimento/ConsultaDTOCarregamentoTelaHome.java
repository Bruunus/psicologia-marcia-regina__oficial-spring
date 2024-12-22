package br.com.psicologia.marcia.DTO.atendimento;

import java.time.LocalDate;

import br.com.psicologia.marcia.model.Perfil;

public class ConsultaDTOCarregamentoTelaHome {
	
	
	private LocalDate dataUltimoAtendimento;
	private Long id;
    private String nomeCompleto;
    private String cpf;
    private Perfil perfil;
 
	
	// 	dados para o JOIN
	
	
	/**
	 * Este contrutor foi carregado para ser utilizado na consulta JPQL realizando 
	 * um JOIN com paciente
	 * @param paciente
	 */
	public ConsultaDTOCarregamentoTelaHome(LocalDate dataUltimoAtendimento, Long id, String nomeCompleto, String cpf, Perfil perfil) {
        this.dataUltimoAtendimento = dataUltimoAtendimento;
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.perfil = perfil;
    }

	

	public LocalDate getDataUltimoAtendimento() {
		return dataUltimoAtendimento;
	}


	public void setDataUltimoAtendimento(LocalDate dataUltimoAtendimento) {
		this.dataUltimoAtendimento = dataUltimoAtendimento;
	}


	public Long getPacienteId() {
		return id;
	}


	public void setPacienteId(Long id) {
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


	public Perfil getPerfil() {
		return perfil;
	}


	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	
	
	
//	@Override
//	public String toString() {
		// TODO Auto-generated method stub
//		return this.dataUltimoAtendimento;
		
//		private Long id;
//	    private String nomeCompleto;
//	    private String cpf;
//	    private Perfil perfil;
//	}
	
	
	
	 
	
	

}
