package br.com.psicologia.marcia.controller.atendimento;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.atendimento.AtendimentoRecord;
import br.com.psicologia.marcia.DTO.atendimento.ConsultaDTOCarregamentoTelaHome;
import br.com.psicologia.marcia.service.atendimento.ReadAtendimentoService;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReadControllerAtendimento {
	
	@Autowired
	private ReadAtendimentoService readAtendimentoService;

	@GetMapping("carregar-tela-home")
	private ResponseEntity<List<ConsultaDTOCarregamentoTelaHome>> carregarDadosTelaHome() {
		List<ConsultaDTOCarregamentoTelaHome> list = readAtendimentoService.carregarTelaHome();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
}
