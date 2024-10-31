package br.com.psicologia.marcia.service.paciente;

import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.DTO.PacienteRecord;

@Service
public class CreatePacienteService {

	private PacienteValidation pacienteValidation;
	
	
	public Boolean cadastrarPaciente(PacienteRecord pacienteDTO) {
		
		pacienteValidation = new PacienteValidation();		 
		if(pacienteValidation.validacaoNaoPodeSerIgual(pacienteDTO)) {
			return false;
		} else {
						
//			Ainda em implementação
			
			
			
			
			
			return true;
		}
		
		

		
		
		 
	}

}
