package br.com.psicologia.marcia.DTO.financeiro;

import br.com.psicologia.marcia.model.enums.TipoDePagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EditarPagamentoPacienteRequest {

    private Long pagamentoId;
    private Integer quantidadeSessao;
    private BigDecimal valorSessao;
    private LocalDate dtDoAtendimento;
    private LocalDate dtPagPrevisto;
    private TipoDePagamento tipoDePagamento;

    public EditarPagamentoPacienteRequest() {
    }

    public Long getPagamentoId() {
        return pagamentoId;
    }

    public void setPagamentoId(Long pagamentoId) {
        this.pagamentoId = pagamentoId;
    }

    public Integer getQuantidadeSessao() {
        return quantidadeSessao;
    }

    public void setQuantidadeSessao(Integer quantidadeSessao) {
        this.quantidadeSessao = quantidadeSessao;
    }

    public BigDecimal getValorSessao() {
        return valorSessao;
    }

    public void setValorSessao(BigDecimal valorSessao) {
        this.valorSessao = valorSessao;
    }

    public LocalDate getDtDoAtendimento() {
        return dtDoAtendimento;
    }

    public void setDtDoAtendimento(LocalDate dtDoAtendimento) {
        this.dtDoAtendimento = dtDoAtendimento;
    }

    public LocalDate getDtPagPrevisto() {
        return dtPagPrevisto;
    }

    public void setDtPagPrevisto(LocalDate dtPagPrevisto) {
        this.dtPagPrevisto = dtPagPrevisto;
    }

    public TipoDePagamento getTipoDePagamento() {
        return tipoDePagamento;
    }

    public void setTipoDePagamento(TipoDePagamento tipoDePagamento) {
        this.tipoDePagamento = tipoDePagamento;
    }
}