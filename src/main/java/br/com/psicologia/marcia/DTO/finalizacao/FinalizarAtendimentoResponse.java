package br.com.psicologia.marcia.DTO.finalizacao;

public record FinalizarAtendimentoResponse(

        Boolean finalizado,
        Boolean pagamentosPendentes,
        String mensagem

) {
}