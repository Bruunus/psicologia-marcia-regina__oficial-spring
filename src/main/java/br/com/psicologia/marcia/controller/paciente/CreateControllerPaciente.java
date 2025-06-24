package br.com.psicologia.marcia.controller.paciente;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.paciente.PacienteRecord;
import br.com.psicologia.marcia.service.paciente.CreatePacienteService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/paciente")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CreateControllerPaciente {

	@Autowired
	private CreatePacienteService createPacienteService;
	 
	@PostMapping("/cadastro")

	public ResponseEntity<?> cadastroDePaciente(@Valid @RequestBody PacienteRecord pacienteRecord) {		
			
		
		try {
			Integer cadastro = createPacienteService.cadastrarPaciente(pacienteRecord);
			if (cadastro == 200) { 
				return ResponseEntity.status(HttpStatus.OK)
						.body(Collections.singletonMap("message", "Cadastro realizado com sucesso"));
			} else if(cadastro == 409) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(Collections.singletonMap("message", "CPF já cadastrado no sistema"));
			} else {
				throw new RuntimeException();			}
		} catch (RuntimeException e) {
			System.err.println("Erro no cadastro de paciente: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(Collections.singletonMap("message", "Erro interno ao realizar cadastro"));
		}
		
//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
}
