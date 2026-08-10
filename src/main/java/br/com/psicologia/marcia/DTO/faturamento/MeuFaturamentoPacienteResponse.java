package br.com.psicologia.marcia.DTO.faturamento;

import java.math.BigDecimal;

public class MeuFaturamentoPacienteResponse {

    private Long pacienteId;
    private String paciente;
    private Long consultasRealizadas;
    private Long acompanhamentosRealizados;
    private Long relatoriosGerados;
    private BigDecimal rendimento;

    public MeuFaturamentoPacienteResponse() {
    }

    public MeuFaturamentoPacienteResponse(
            Long pacienteId,
            String paciente,
            Long consultasRealizadas,
            Long acompanhamentosRealizados,
            Long relatoriosGerados,
            BigDecimal rendimento
    ) {
        this.pacienteId = pacienteId;
        this.paciente = paciente;
        this.consultasRealizadas = consultasRealizadas;
        this.acompanhamentosRealizados = acompanhamentosRealizados;
        this.relatoriosGerados = relatoriosGerados;
        this.rendimento = rendimento;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public Long getConsultasRealizadas() {
        return consultasRealizadas;
    }

    public void setConsultasRealizadas(Long consultasRealizadas) {
        this.consultasRealizadas = consultasRealizadas;
    }

    public Long getAcompanhamentosRealizados() {
        return acompanhamentosRealizados;
    }

    public void setAcompanhamentosRealizados(Long acompanhamentosRealizados) {
        this.acompanhamentosRealizados = acompanhamentosRealizados;
    }

    public Long getRelatoriosGerados() {
        return relatoriosGerados;
    }

    public void setRelatoriosGerados(Long relatoriosGerados) {
        this.relatoriosGerados = relatoriosGerados;
    }

    public BigDecimal getRendimento() {
        return rendimento;
    }

    public void setRendimento(BigDecimal rendimento) {
        this.rendimento = rendimento;
    }
}