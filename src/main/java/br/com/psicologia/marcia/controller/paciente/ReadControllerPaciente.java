package br.com.psicologia.marcia.controller.paciente;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.atendimento.ConsultaDTOCarregamentoTelaHome;
import br.com.psicologia.marcia.DTO.paciente.PacienteRecord;
import br.com.psicologia.marcia.service.paciente.ReadPacienteService;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReadControllerPaciente {
	
	@Autowired
	private ReadPacienteService readPacienteService;
		
	
	@GetMapping("carregar-tela-home")
	private ResponseEntity<List<ConsultaDTOCarregamentoTelaHome>> carregarDadosTelaHome() {
		List<ConsultaDTOCarregamentoTelaHome> list = readPacienteService.carregarTelaHome();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@GetMapping("search")
	public ResponseEntity<?> pesquisaDePaciente(@RequestParam("name") String nome) {
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	
	@GetMapping("pacientes")
	public ResponseEntity<List<PacienteRecord>> getPacientes() {
		List<PacienteRecord> list = readPacienteService.carregarListaPaciente();
		return new ResponseEntity<>(list, HttpStatus.OK);	
	}
	
}
