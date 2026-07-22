package br.com.psicologia.marcia.DTO.financeiro;

import br.com.psicologia.marcia.model.enums.StatusPagamento;
import br.com.psicologia.marcia.model.enums.TipoDePagamento;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagamentoPacienteResponse {

    private Long id;
    private Long pacienteId;

    private Integer quantidadeSessao;
    private BigDecimal valorSessao;

    private LocalDate dtDoAtendimento;
    private LocalDate dtPagPrevisto;

    private TipoDePagamento tipoDePagamento;
    private String tipoDePagamentoDescricao;

    private StatusPagamento statusPagamento;

    @JsonProperty("statusDescricao")
    private String statusDescricao;

    @JsonProperty("statusCor")
    private String statusCor;

    public PagamentoPacienteResponse() {
    }

    public Long getId() {
        return id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public Integer getQuantidadeSessao() {
        return quantidadeSessao;
    }

    public BigDecimal getValorSessao() {
        return valorSessao;
    }

    public LocalDate getDtDoAtendimento() {
        return dtDoAtendimento;
    }

    public LocalDate getDtPagPrevisto() {
        return dtPagPrevisto;
    }

    public TipoDePagamento getTipoDePagamento() {
        return tipoDePagamento;
    }

    public String getTipoDePagamentoDescricao() {
        return tipoDePagamentoDescricao;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public String getStatusDescricao() {
        return statusDescricao;
    }

    public String getStatusCor() {
        return statusCor;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public void setQuantidadeSessao(Integer quantidadeSessao) {
        this.quantidadeSessao = quantidadeSessao;
    }

    public void setValorSessao(BigDecimal valorSessao) {
        this.valorSessao = valorSessao;
    }

    public void setDtDoAtendimento(LocalDate dtDoAtendimento) {
        this.dtDoAtendimento = dtDoAtendimento;
    }

    public void setDtPagPrevisto(LocalDate dtPagPrevisto) {
        this.dtPagPrevisto = dtPagPrevisto;
    }

    public void setTipoDePagamento(TipoDePagamento tipoDePagamento) {
        this.tipoDePagamento = tipoDePagamento;
    }

    public void setTipoDePagamentoDescricao(String tipoDePagamentoDescricao) {
        this.tipoDePagamentoDescricao = tipoDePagamentoDescricao;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public void setStatusDescricao(String statusDescricao) {
        this.statusDescricao = statusDescricao;
    }

    public void setStatusCor(String statusCor) {
        this.statusCor = statusCor;
    }
}