package br.com.psicologia.marcia.repository.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.psicologia.marcia.model.Analitics;

/**
 * Repositório responsável pelas operações com a entidade Analitics.
 */
@Repository
public interface RequisicaoAnaliticaRepository extends JpaRepository<Analitics, Long> {
}
