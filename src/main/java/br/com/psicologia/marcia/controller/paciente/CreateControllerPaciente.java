package br.com.psicologia.marcia.controller.paciente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.PacienteRecord;
import br.com.psicologia.marcia.service.paciente.CreatePacienteService;



@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CreateControllerPaciente {

	@Autowired
	private CreatePacienteService createPacienteService;
	 
	@PostMapping("/cadastro/paciente")
	@CrossOrigin(methods = RequestMethod.POST)
	public ResponseEntity<?> cadastroDePaciente(@RequestBody PacienteRecord pacienteRecord) {	
		Boolean cadastrarPaciente = createPacienteService.cadastrarPaciente(pacienteRecord);		
		
		try {
			if(cadastrarPaciente) {
				return ResponseEntity.ok().build();
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Erro interno ao realizar cadastro");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return null;
		
	}
	
}
