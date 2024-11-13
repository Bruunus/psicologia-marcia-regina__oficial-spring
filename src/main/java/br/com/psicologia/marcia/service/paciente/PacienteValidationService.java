package br.com.psicologia.marcia.service.paciente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.repository.paciente.ReadPacienteRepository;

@Service
public class PacienteValidationService  {

	@Autowired
	private ReadPacienteRepository readPacienteRepository;
	
	protected ResponseEntity<?> validacaoNaoPodeSerIgual(String nomeCompleto) {
		
//		Retire estas anotações somente depois de ter passado para a documentação
		
		// esta validação precisa verificar se este novo paciente já possui cadastro no sistema
		// Se sim não pode passar desse bloco
		
	 
		
		// 1 - Ir no banco e puxar todos os usuários
		Integer pacienteCount = 
				readPacienteRepository.buscaDePacienteDuplicado(nomeCompleto);
		
		// Realize uma condição, caso boleana para verificar ... 
		if(pacienteCount > 0) {
			System.out.println("Paciente já cadastrado");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("O paciente já está cadastrado!");
		}
		// Porém esta validação só deverá ser solta após os testes finais pois 
//		com teste repetitivo ela não vai deixar passar
		
		
		return ResponseEntity.ok().build();
		
		
	}
	
	
}
