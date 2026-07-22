package br.com.psicologia.marcia.DTO.financeiro;

public class DeletarPagamentoPacienteRequest {

    private Long pagamentoId;

    public DeletarPagamentoPacienteRequest() {
    }

    public Long getPagamentoId() {
        return pagamentoId;
    }

    public void setPagamentoId(Long pagamentoId) {
        this.pagamentoId = pagamentoId;
    }
}