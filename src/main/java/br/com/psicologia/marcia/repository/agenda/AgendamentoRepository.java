package br.com.psicologia.marcia.repository.agenda;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.psicologia.marcia.model.Agendamento;

@Repository
public interface AgendamentoRepository
    extends JpaRepository<Agendamento, Long> {

    @EntityGraph(attributePaths = "paciente")
    List<Agendamento>
        findByDataAgendamentoOrderByHorarioAsc(
            LocalDate dataAgendamento
        );

    @EntityGraph(attributePaths = "paciente")
    List<Agendamento>
        findByDataAgendamentoBetweenOrderByDataAgendamentoAscHorarioAsc(
            LocalDate dataInicial,
            LocalDate dataFinal
        );

    @EntityGraph(attributePaths = "paciente")
    List<Agendamento>
        findAllByOrderByDataAgendamentoAscHorarioAsc();

    @EntityGraph(attributePaths = "paciente")
    List<Agendamento>
        findByDataAgendamentoAndHorarioBetweenOrderByHorarioAsc(
            LocalDate dataAgendamento,
            LocalTime horarioInicial,
            LocalTime horarioFinal
        );

    long countByDataAgendamento(
        LocalDate dataAgendamento
    );

    long deleteByDataAgendamentoBefore(
        LocalDate dataLimite
    );
}