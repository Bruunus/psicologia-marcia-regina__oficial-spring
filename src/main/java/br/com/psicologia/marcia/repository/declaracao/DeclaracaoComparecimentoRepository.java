package br.com.psicologia.marcia.repository.declaracao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.psicologia.marcia.model.DeclaracaoComparecimento;
import br.com.psicologia.marcia.model.enums.StatusDelete;

public interface DeclaracaoComparecimentoRepository extends JpaRepository<DeclaracaoComparecimento, Long> {

    Optional<DeclaracaoComparecimento> findByIdAndStatusDelete(Long id, StatusDelete statusDelete);
}