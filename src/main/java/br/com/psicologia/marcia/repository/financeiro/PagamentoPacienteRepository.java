package br.com.psicologia.marcia.repository.financeiro;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.psicologia.marcia.model.PagamentoPaciente;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.model.enums.StatusPagamento;

public interface PagamentoPacienteRepository
        extends JpaRepository<PagamentoPaciente, Long> {

    List<PagamentoPaciente>
            findByPacienteIdAndStatusDeleteOrderByDtPagPrevistoDesc(
                    Long pacienteId,
                    StatusDelete statusDelete
            );

    Optional<PagamentoPaciente> findByIdAndStatusDelete(
            Long id,
            StatusDelete statusDelete
    );

    @Query("""
        SELECT COALESCE(SUM(p.quantidadeSessao), 0)
        FROM PagamentoPaciente p
        WHERE p.paciente.id = :pacienteId
          AND p.statusDelete = :statusDelete
    """)
    Long somarConsultasRealizadas(
            @Param("pacienteId") Long pacienteId,
            @Param("statusDelete") StatusDelete statusDelete
    );

    @Query("""
        SELECT COALESCE(
            SUM(p.valorSessao * p.quantidadeSessao),
            0
        )
        FROM PagamentoPaciente p
        WHERE p.paciente.id = :pacienteId
          AND p.statusPagamento = :statusPagamento
          AND p.statusDelete = :statusDelete
    """)
    BigDecimal somarRendimentoPago(
            @Param("pacienteId") Long pacienteId,
            @Param("statusPagamento") StatusPagamento statusPagamento,
            @Param("statusDelete") StatusDelete statusDelete
    );
}