package br.com.psicologia.marcia.service.paciente;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.psicologia.marcia.DTO.PacienteRecord;
import br.com.psicologia.marcia.repository.paciente.ReadPacienteRepository;

public class PacienteValidation  {

	@Autowired
	private ReadPacienteRepository readPacienteRepository;
	
	protected Boolean validacaoNaoPodeSerIgual(PacienteRecord pacienteDTO) {
		
//		Retire estas anotações somente depois de ter passado para a documentação
		
		// esta validação precisa verificar se este novo paciente já possui cadastro no sistema
		// Se sim não pode passar desse bloco
		
		// 1 - Ir no banco e puxar todos os usuários
		// 2 - realize uma iteração e no meio dela pesquise pelo usuário 
		// Realize uma condição, caso boleana para verificar ... 
		
		
		return true;
		
		
	}
	
	
}
