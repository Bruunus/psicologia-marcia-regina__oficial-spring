package br.com.psicologia.marcia.DTO.declaracao;

import java.time.LocalDate;
import java.time.LocalTime;

import br.com.psicologia.marcia.model.enums.OrientacaoDeclaracaoComparecimento;

public class DeclaracaoComparecimentoRequest {

    private Long pacienteId;
    private LocalDate dataComparecimento;
    private LocalTime horaInicio;
    private LocalTime horaTermino;
    private OrientacaoDeclaracaoComparecimento orientacao;
    private Integer quantidadeDiasRepouso;
    private LocalDate dataEmissao;

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public LocalDate getDataComparecimento() {
        return dataComparecimento;
    }

    public void setDataComparecimento(LocalDate dataComparecimento) {
        this.dataComparecimento = dataComparecimento;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraTermino() {
        return horaTermino;
    }

    public void setHoraTermino(LocalTime horaTermino) {
        this.horaTermino = horaTermino;
    }

    public OrientacaoDeclaracaoComparecimento getOrientacao() {
        return orientacao;
    }

    public void setOrientacao(OrientacaoDeclaracaoComparecimento orientacao) {
        this.orientacao = orientacao;
    }

    public Integer getQuantidadeDiasRepouso() {
        return quantidadeDiasRepouso;
    }

    public void setQuantidadeDiasRepouso(Integer quantidadeDiasRepouso) {
        this.quantidadeDiasRepouso = quantidadeDiasRepouso;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }
}