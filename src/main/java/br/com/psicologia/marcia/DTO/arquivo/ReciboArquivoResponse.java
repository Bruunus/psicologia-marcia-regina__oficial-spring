package br.com.psicologia.marcia.DTO.arquivo;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.psicologia.marcia.model.enums.StatusPagamento;
import br.com.psicologia.marcia.model.enums.TipoDePagamento;

public record ReciboArquivoResponse(
        Long pagamentoId,
        LocalDate dtBaixaPagamento,
        LocalDate dtPagPrevisto,
        BigDecimal valorSessao,
        TipoDePagamento tipoDePagamento,
        StatusPagamento statusPagamento
) {
}