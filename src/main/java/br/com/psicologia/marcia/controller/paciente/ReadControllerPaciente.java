package br.com.psicologia.marcia.controller.paciente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReadControllerPaciente {

	@GetMapping("search")
	public ResponseEntity<?> pesquisaDePaciente(@RequestParam("name") String nome) {
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
