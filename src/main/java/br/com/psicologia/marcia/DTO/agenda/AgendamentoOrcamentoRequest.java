package br.com.psicologia.marcia.DTO.agenda;

import java.time.LocalDate;
import java.time.LocalTime;

public class AgendamentoOrcamentoRequest {

    private String nomePessoa;
    private LocalDate dataAgendamento;
    private LocalTime horario;
    private Boolean confirmarConflito;

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(
        LocalDate dataAgendamento
    ) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public Boolean getConfirmarConflito() {
        return confirmarConflito;
    }

    public void setConfirmarConflito(
        Boolean confirmarConflito
    ) {
        this.confirmarConflito = confirmarConflito;
    }
}