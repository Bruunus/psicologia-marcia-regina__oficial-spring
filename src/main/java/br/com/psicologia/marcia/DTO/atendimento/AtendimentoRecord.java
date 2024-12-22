package br.com.psicologia.marcia.DTO.atendimento;

import java.time.LocalDate;

import br.com.psicologia.marcia.model.Paciente;

public record AtendimentoRecord(
		
		LocalDate dataUltimoAtendimento,
		Paciente paciente 
		
		) {
	
 

}
