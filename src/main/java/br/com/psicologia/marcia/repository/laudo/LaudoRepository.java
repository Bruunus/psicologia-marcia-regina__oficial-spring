package br.com.psicologia.marcia.repository.laudo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.psicologia.marcia.DTO.laudo.LaudoListaResponse;
import br.com.psicologia.marcia.model.Laudo;


public interface LaudoRepository
    extends JpaRepository<Laudo, Long> {


    /*
     * ============================================================
     * LISTAGEM ENXUTA PARA O MODAL "VISUALIZAR LAUDOS"
     * ============================================================
     *
     * Busca somente:
     *
     * - ID do laudo
     * - Data/hora de criação
     *
     * Não carrega o restante do registro.
     */

    @Query("""
        SELECT new br.com.psicologia.marcia.DTO.laudo.LaudoListaResponse(
            l.id,
            l.dataCriacao
        )
        FROM Laudo l
        WHERE l.paciente.id = :pacienteId
          AND l.statusDelete = false
        ORDER BY l.dataCriacao DESC
        """)
    List<LaudoListaResponse> listarResumoPorPaciente(
        @Param("pacienteId")
        Long pacienteId
    );


    /*
     * ============================================================
     * LISTAGEM COMPLETA PARA O ARQUIVO DO PACIENTE
     * ============================================================
     */

    List<Laudo>
        findByPacienteIdAndStatusDeleteFalseOrderByDataCriacaoDesc(
            Long pacienteId
        );


    /*
     * ============================================================
     * BUSCAR LAUDO COMPLETO
     * ============================================================
     */

    Optional<Laudo>
        findByIdAndStatusDeleteFalse(
            Long id
        );


    /*
     * ============================================================
     * VERIFICAR EXISTÊNCIA
     * ============================================================
     */

    boolean
        existsByIdAndStatusDeleteFalse(
            Long id
        );
}