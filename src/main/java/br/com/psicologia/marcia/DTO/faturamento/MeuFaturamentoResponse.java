package br.com.psicologia.marcia.DTO.faturamento;

import java.math.BigDecimal;
import java.util.List;

public class MeuFaturamentoResponse {

    private List<MeuFaturamentoPacienteResponse> pacientes;
    private Long totalConsultasRealizadas;
    private Long totalAcompanhamentosRealizados;
    private Long totalRelatoriosGerados;
    private BigDecimal rendimentoTotal;

    public MeuFaturamentoResponse() {
    }

    public MeuFaturamentoResponse(
            List<MeuFaturamentoPacienteResponse> pacientes,
            Long totalConsultasRealizadas,
            Long totalAcompanhamentosRealizados,
            Long totalRelatoriosGerados,
            BigDecimal rendimentoTotal
    ) {
        this.pacientes = pacientes;
        this.totalConsultasRealizadas = totalConsultasRealizadas;
        this.totalAcompanhamentosRealizados = totalAcompanhamentosRealizados;
        this.totalRelatoriosGerados = totalRelatoriosGerados;
        this.rendimentoTotal = rendimentoTotal;
    }

    public List<MeuFaturamentoPacienteResponse> getPacientes() {
        return pacientes;
    }

    public void setPacientes(List<MeuFaturamentoPacienteResponse> pacientes) {
        this.pacientes = pacientes;
    }

    public Long getTotalConsultasRealizadas() {
        return totalConsultasRealizadas;
    }

    public void setTotalConsultasRealizadas(Long totalConsultasRealizadas) {
        this.totalConsultasRealizadas = totalConsultasRealizadas;
    }

    public Long getTotalAcompanhamentosRealizados() {
        return totalAcompanhamentosRealizados;
    }

    public void setTotalAcompanhamentosRealizados(
            Long totalAcompanhamentosRealizados
    ) {
        this.totalAcompanhamentosRealizados = totalAcompanhamentosRealizados;
    }

    public Long getTotalRelatoriosGerados() {
        return totalRelatoriosGerados;
    }

    public void setTotalRelatoriosGerados(Long totalRelatoriosGerados) {
        this.totalRelatoriosGerados = totalRelatoriosGerados;
    }

    public BigDecimal getRendimentoTotal() {
        return rendimentoTotal;
    }

    public void setRendimentoTotal(BigDecimal rendimentoTotal) {
        this.rendimentoTotal = rendimentoTotal;
    }
}