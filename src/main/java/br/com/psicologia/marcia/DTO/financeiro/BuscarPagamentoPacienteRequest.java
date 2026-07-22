package br.com.psicologia.marcia.DTO.financeiro;

public class BuscarPagamentoPacienteRequest {

    private Long pagamentoId;

    public BuscarPagamentoPacienteRequest() {
    }

    public Long getPagamentoId() {
        return pagamentoId;
    }

    public void setPagamentoId(Long pagamentoId) {
        this.pagamentoId = pagamentoId;
    }
}