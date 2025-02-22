package br.com.psicologia.marcia.controller.paciente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.paciente.PacienteRecord;
import br.com.psicologia.marcia.DTO.paciente.PacienteUpdateRecord;
import br.com.psicologia.marcia.service.paciente.UpdatePacienteService;

@RestController
@RequestMapping("/paciente")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UpdateControllerPaciente {
	
	@Autowired
	private UpdatePacienteService updatePacienteService;
	
	
	@PutMapping("/atualizar")
	public ResponseEntity<?> atualizarPaciente(@RequestBody PacienteUpdateRecord pacienteRecord) {
		PacienteUpdateRecord pacienteAtualizado = updatePacienteService.updatePatient(pacienteRecord);
		return ResponseEntity.ok(pacienteAtualizado);
	}
	
	
}
