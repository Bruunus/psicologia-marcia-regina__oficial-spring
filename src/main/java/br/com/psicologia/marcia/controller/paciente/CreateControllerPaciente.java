package br.com.psicologia.marcia.controller.paciente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.PacienteRecord;
import br.com.psicologia.marcia.service.paciente.CreatePacienteService;
import jakarta.validation.Valid;

 



@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CreateControllerPaciente {

	@Autowired
	private CreatePacienteService createPacienteService;
	 
	@PostMapping("cadastro/paciente")
	 
	public ResponseEntity<?> cadastroDePaciente(@Valid @RequestBody PacienteRecord pacienteRecord) {		
				
		return createPacienteService.cadastrarPaciente(pacienteRecord);		

//			if(cadastrarPaciente) {
//				return ResponseEntity.status(HttpStatus.OK).build();	
//			} else {
//				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//						.body("Erro interno ao realizar cadastro");
//			}
		
	}
	
}
