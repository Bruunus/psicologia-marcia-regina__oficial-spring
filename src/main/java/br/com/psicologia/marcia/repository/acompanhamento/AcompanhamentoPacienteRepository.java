package br.com.psicologia.marcia.repository.acompanhamento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.psicologia.marcia.model.AcompanhamentoPaciente;
import br.com.psicologia.marcia.model.enums.StatusDelete;

@Repository
public interface AcompanhamentoPacienteRepository
        extends JpaRepository<AcompanhamentoPaciente, Long> {

    List<AcompanhamentoPaciente>
            findByStatusDeleteOrderByDataAcompanhamentoDesc(
                    StatusDelete statusDelete
            );

    List<AcompanhamentoPaciente>
            findByPacienteIdAndStatusDeleteOrderByDataAcompanhamentoDesc(
                    Long pacienteId,
                    StatusDelete statusDelete
            );

    List<AcompanhamentoPaciente>
            findByPacienteIdAndStatusDeleteOrderByDataAcompanhamentoAsc(
                    Long pacienteId,
                    StatusDelete statusDelete
            );

    Optional<AcompanhamentoPaciente> findByIdAndStatusDelete(
            Long id,
            StatusDelete statusDelete
    );

    Optional<AcompanhamentoPaciente>
            findByPacienteIdAndDataAcompanhamentoAndPacienteAusenteFalseAndStatusDelete(
                    Long pacienteId,
                    LocalDateTime dataAcompanhamento,
                    StatusDelete statusDelete
            );

    List<AcompanhamentoPaciente>
            findByPacienteIdAndPacienteAusenteFalseAndStatusDeleteOrderByDataAcompanhamentoDesc(
                    Long pacienteId,
                    StatusDelete statusDelete
            );

    Long countByPacienteIdAndPacienteAusenteFalseAndStatusDelete(
            Long pacienteId,
            StatusDelete statusDelete
    );
}