package br.com.psicologia.marcia.controller.paciente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.PacienteRecord;
import br.com.psicologia.marcia.service.paciente.PacienteService;



@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ControllerPaciente {

	@Autowired
	private PacienteService pacienteService;
	 
	@PostMapping("/paciente")
	@CrossOrigin(methods = RequestMethod.POST)
	public ResponseEntity<PacienteRecord> cadastroDePaciente(@RequestBody PacienteRecord pacienteDTO) {	
		pacienteService.cadastrarPaciente(pacienteDTO);
		return ResponseEntity.ok().build();
	}
	
}
