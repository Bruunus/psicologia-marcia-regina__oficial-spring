package br.com.psicologia.marcia.DTO.financeiro;

public class ConfirmarPagamentoRequest {

    private Long pagamentoId;

    public ConfirmarPagamentoRequest() {
    }

    public Long getPagamentoId() {
        return pagamentoId;
    }

    public void setPagamentoId(Long pagamentoId) {
        this.pagamentoId = pagamentoId;
    }
}