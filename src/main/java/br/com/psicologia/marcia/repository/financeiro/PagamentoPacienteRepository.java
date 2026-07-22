package br.com.psicologia.marcia.repository.financeiro;

import br.com.psicologia.marcia.model.PagamentoPaciente;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagamentoPacienteRepository extends JpaRepository<PagamentoPaciente, Long> {

    List<PagamentoPaciente> findByPacienteIdAndStatusDeleteOrderByDtPagPrevistoDesc(
            Long pacienteId,
            StatusDelete statusDelete
    );

    Optional<PagamentoPaciente> findByIdAndStatusDelete(
            Long id,
            StatusDelete statusDelete
    );
}