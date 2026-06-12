package br.com.psicologia.marcia.repository.acompanhamento;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.psicologia.marcia.model.AcompanhamentoPaciente;
import br.com.psicologia.marcia.model.enums.StatusDelete;

@Repository
public interface AcompanhamentoPacienteRepository extends JpaRepository<AcompanhamentoPaciente, Long> {

    List<AcompanhamentoPaciente> findByPacienteIdAndStatusDeleteOrderByDataAcompanhamentoDesc(
            Long pacienteId,
            StatusDelete statusDelete
    );

    Optional<AcompanhamentoPaciente> findByIdAndStatusDelete(
            Long id,
            StatusDelete statusDelete
    );

    Optional<AcompanhamentoPaciente> findByPacienteIdAndDataAcompanhamentoAndStatusDelete(
            Long pacienteId,
            LocalDate dataAcompanhamento,
            StatusDelete statusDelete
    );

    List<AcompanhamentoPaciente> findByPacienteIdAndPacienteAusenteFalseAndStatusDeleteOrderByDataAcompanhamentoDesc(
            Long pacienteId,
            StatusDelete statusDelete
    );
}