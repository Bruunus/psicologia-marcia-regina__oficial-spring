package br.com.psicologia.marcia.controller.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.model.BugsEmanutencao;
import br.com.psicologia.marcia.repository.app.ManutencaoInterface;

@RestController
@RequestMapping("/")
public class ManutencaoController {

	@Autowired
	private ManutencaoInterface manutencaoInterface;
	
	
	@PostMapping("ocorrencia/registrar")
	public ResponseEntity<?> registrarProblema(@RequestBody BugsEmanutencao problema) {		
		try {
			manutencaoInterface.save(problema);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}			
	}
	
	
	
	@GetMapping("ocorrencia/carregar")
	public ResponseEntity<?> carrregarOcorrencias() {
		List<BugsEmanutencao> list = manutencaoInterface.findByDeletadoFalse();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	
	
	
	@DeleteMapping("ocorrencia/finalizar")
	public ResponseEntity<String> finalizarBug(@RequestBody BugsEmanutencao bug) {
	    try {
	        // Verifica se existe algum Bug com a tela e o problema fornecidos E que não tenha sido marcado como deletado
	        BugsEmanutencao existingBug = manutencaoInterface.findByTelaAndProblemaAndDeletadoFalse(bug.getTela(), bug.getProblema());

	        if (existingBug != null) {
	            // Marca o bug como deletado (não deleta fisicamente)
	            existingBug.setDeletado(true);
	            manutencaoInterface.save(existingBug); // Atualiza o bug no banco de dados
	            return ResponseEntity.ok("Problema marcado como excluído com sucesso!");
	        } else {
	            return ResponseEntity.status(404).body("Problema não encontrado ou já marcado como excluído.");
	        }
	    } catch (Exception e) {
	        // Tratar exceções e retornar uma resposta adequada
	        return ResponseEntity.status(500).body("Erro ao excluir o problema: " + e.getMessage());
	    }
	}

	
	 
	
}
