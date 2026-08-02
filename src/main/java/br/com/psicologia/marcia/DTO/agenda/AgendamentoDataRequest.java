package br.com.psicologia.marcia.DTO.agenda;

import java.time.LocalDate;

public class AgendamentoDataRequest {

    private LocalDate dataAgendamento;

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(
        LocalDate dataAgendamento
    ) {
        this.dataAgendamento = dataAgendamento;
    }
}