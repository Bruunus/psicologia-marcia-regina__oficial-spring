package br.com.psicologia.marcia.DTO.declaracao;

import java.time.LocalDate;
import java.time.LocalTime;

import br.com.psicologia.marcia.model.enums.OrientacaoDeclaracaoComparecimento;
import br.com.psicologia.marcia.model.enums.StatusDelete;

public class DeclaracaoComparecimentoResponse {

    private Long id;
    private Long pacienteId;
    private String nomePaciente;
    private LocalDate dataComparecimento;
    private LocalTime horaInicio;
    private LocalTime horaTermino;
    private OrientacaoDeclaracaoComparecimento orientacao;
    private Integer quantidadeDiasRepouso;
    private LocalDate dataEmissao;
    private StatusDelete statusDelete;

    public DeclaracaoComparecimentoResponse(
            Long id,
            Long pacienteId,
            String nomePaciente,
            LocalDate dataComparecimento,
            LocalTime horaInicio,
            LocalTime horaTermino,
            OrientacaoDeclaracaoComparecimento orientacao,
            Integer quantidadeDiasRepouso,
            LocalDate dataEmissao,
            StatusDelete statusDelete
    ) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.nomePaciente = nomePaciente;
        this.dataComparecimento = dataComparecimento;
        this.horaInicio = horaInicio;
        this.horaTermino = horaTermino;
        this.orientacao = orientacao;
        this.quantidadeDiasRepouso = quantidadeDiasRepouso;
        this.dataEmissao = dataEmissao;
        this.statusDelete = statusDelete;
    }

    public Long getId() {
        return id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public LocalDate getDataComparecimento() {
        return dataComparecimento;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraTermino() {
        return horaTermino;
    }

    public OrientacaoDeclaracaoComparecimento getOrientacao() {
        return orientacao;
    }

    public Integer getQuantidadeDiasRepouso() {
        return quantidadeDiasRepouso;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public StatusDelete getStatusDelete() {
        return statusDelete;
    }
}