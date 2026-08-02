package br.com.psicologia.marcia.DTO.agenda;

import java.time.LocalDate;

public class AgendamentoPeriodoRequest {

    private LocalDate dataInicial;
    private LocalDate dataFinal;

    public LocalDate getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(
        LocalDate dataInicial
    ) {
        this.dataInicial = dataInicial;
    }

    public LocalDate getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(
        LocalDate dataFinal
    ) {
        this.dataFinal = dataFinal;
    }
}