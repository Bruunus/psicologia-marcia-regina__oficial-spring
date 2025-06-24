package br.com.psicologia.marcia.controller.paciente;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.atendimento.ConsultaDTOCarregamentoTelaHome;
import br.com.psicologia.marcia.DTO.paciente.PacienteRecord;
import br.com.psicologia.marcia.service.paciente.ReadPacienteService;

@RestController
@RequestMapping("/paciente")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReadControllerPaciente {
	
	@Autowired
	private ReadPacienteService readPacienteService;
		
	
	@GetMapping("/carregar-tela-home")
	public ResponseEntity<?> carregarDadosTelaHome() {
	    try {
	        List<ConsultaDTOCarregamentoTelaHome> list = readPacienteService.carregarTelaHome();
	        return ResponseEntity.ok(list);
	    } catch (Exception e) {
	        System.err.println("Erro ao carregar tela home: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(Collections.singletonMap("message", "Erro ao carregar dados da tela inicial"));
	    }
	}


	@GetMapping("/search")
	public ResponseEntity<?> pesquisaDePaciente(@RequestParam("name") String nome) {
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	
	/**
	 * Controlador responsável por trazer os dados do paciente para a tela de "Identificação"
	 * @param cpf
	 * @return
	 */
	@PostMapping("/carregar-dados")
	public ResponseEntity<Object>  getPacientes(@RequestBody String cpf) {
		
		try {
			
//			Thread.sleep(8000); 
			
			PacienteRecord paciente = readPacienteService.carregarPaciente(cpf);
			
			if(paciente == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
			} else {
				return new ResponseEntity<>(paciente, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Erro ao processar requisição. "+e,HttpStatus.BAD_REQUEST);
		}
		
			
	}
	
}
