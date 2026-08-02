package br.com.psicologia.marcia.DTO.agenda;

import java.time.LocalDate;

public class AgendamentoCalendarioDiaResponse {

    private LocalDate dataAgendamento;
    private int quantidadeTotal;
    private int quantidadeConsultas;
    private int quantidadeOrcamentos;

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(
        LocalDate dataAgendamento
    ) {
        this.dataAgendamento = dataAgendamento;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(
        int quantidadeTotal
    ) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public int getQuantidadeConsultas() {
        return quantidadeConsultas;
    }

    public void setQuantidadeConsultas(
        int quantidadeConsultas
    ) {
        this.quantidadeConsultas =
            quantidadeConsultas;
    }

    public int getQuantidadeOrcamentos() {
        return quantidadeOrcamentos;
    }

    public void setQuantidadeOrcamentos(
        int quantidadeOrcamentos
    ) {
        this.quantidadeOrcamentos =
            quantidadeOrcamentos;
    }
}