package br.com.psicologia.marcia.repository.app;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.psicologia.marcia.model.BugsEmanutencao;

public interface ManutencaoInterface extends JpaRepository <BugsEmanutencao, Long> {

	List<BugsEmanutencao> findByDeletadoFalse();

	BugsEmanutencao findByTelaAndProblemaAndDeletadoFalse(String tela, String problema);
	
}
