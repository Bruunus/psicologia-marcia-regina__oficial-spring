package br.com.psicologia.marcia.repository.atendimento;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.psicologia.marcia.DTO.atendimento.ConsultaDTOCarregamentoTelaHome;
import br.com.psicologia.marcia.model.Atendimento;
import br.com.psicologia.marcia.model.enums.StatusPaciente;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    @Query("""
        SELECT new br.com.psicologia.marcia.DTO.atendimento.ConsultaDTOCarregamentoTelaHome(
            MAX(ate.dataUltimoAtendimento),
            pac.id,
            pac.nomeCompleto,
            pac.cpf,
            pac.perfil,
            pac.idade,
            pac.atendimentoIniciado
        )
        FROM Paciente pac
        LEFT JOIN pac.atendimentos ate
        WHERE pac.statusPaciente = :statusPaciente
        GROUP BY
            pac.id,
            pac.nomeCompleto,
            pac.cpf,
            pac.perfil,
            pac.idade,
            pac.atendimentoIniciado
    """)
    List<ConsultaDTOCarregamentoTelaHome> carregarListaHome(
            @Param("statusPaciente") StatusPaciente statusPaciente
    );

    Optional<Atendimento> findFirstByPaciente_IdOrderByIdDesc(
            Long pacienteId
    );
}
