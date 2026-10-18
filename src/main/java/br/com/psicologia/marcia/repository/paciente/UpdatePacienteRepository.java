
package br.com.psicologia.marcia.repository.paciente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.enums.StatusPaciente;

public interface UpdatePacienteRepository
        extends JpaRepository<Paciente, Long> {

    @Modifying
    @Query("""
        UPDATE Paciente p
        SET p.statusPaciente = :novoStatus
        WHERE p.id = :pacienteId
          AND p.statusPaciente = :statusAtual
        """)
    int atualizarStatusPaciente(
            @Param("pacienteId") Long pacienteId,
            @Param("statusAtual") StatusPaciente statusAtual,
            @Param("novoStatus") StatusPaciente novoStatus
    );
}
