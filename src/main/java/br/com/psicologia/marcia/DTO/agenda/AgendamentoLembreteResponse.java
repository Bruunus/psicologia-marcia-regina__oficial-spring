package br.com.psicologia.marcia.DTO.agenda;

import java.time.LocalDate;

public class AgendamentoLembreteResponse {

    private LocalDate dataReferencia;
    private long quantidade;

    public AgendamentoLembreteResponse() {
    }

    public AgendamentoLembreteResponse(
        LocalDate dataReferencia,
        long quantidade
    ) {
        this.dataReferencia = dataReferencia;
        this.quantidade = quantidade;
    }

    public LocalDate getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(
        LocalDate dataReferencia
    ) {
        this.dataReferencia = dataReferencia;
    }

    public long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(long quantidade) {
        this.quantidade = quantidade;
    }
}