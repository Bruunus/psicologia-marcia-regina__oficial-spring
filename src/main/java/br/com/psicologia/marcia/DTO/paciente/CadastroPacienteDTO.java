package br.com.psicologia.marcia.DTO.paciente;

import br.com.psicologia.marcia.DTO.endereco.EnderecoRecord;
import br.com.psicologia.marcia.DTO.queixa.QueixaRecord;

public class CadastroPacienteDTO {
	
	private PacienteRecord pacienteRecord;
	
	private EnderecoRecord enderecoRecord;
	
	private QueixaRecord queixaRecord;
	

	public PacienteRecord getPacienteRecord() {
		return pacienteRecord;
	}

	public void setPacienteRecord(PacienteRecord pacienteRecord) {
		this.pacienteRecord = pacienteRecord;
	}

	public EnderecoRecord getEnderecoRecord() {
		return enderecoRecord;
	}

	public void setEnderecoRecord(EnderecoRecord enderecoRecord) {
		this.enderecoRecord = enderecoRecord;
	}

	public QueixaRecord getQueixaRecord() {
		return queixaRecord;
	}

	public void setQueixaRecord(QueixaRecord queixaRecord) {
		this.queixaRecord = queixaRecord;
	}
	
	

}
