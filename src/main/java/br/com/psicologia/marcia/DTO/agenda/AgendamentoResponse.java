package br.com.psicologia.marcia.DTO.agenda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import br.com.psicologia.marcia.model.enums.TipoAgendamento;
import br.com.psicologia.marcia.model.enums.TipoRecorrencia;

public class AgendamentoResponse {

    private Long id;
    private Long pacienteId;
    private String pacientePessoa;
    private LocalDate dataAgendamento;
    private LocalTime horario;
    private TipoAgendamento tipoAgendamento;
    private TipoRecorrencia tipoRecorrencia;
    private Integer quantidadeRecorrencia;
    private String grupoRecorrencia;
    private LocalDateTime dataCriacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getPacientePessoa() {
        return pacientePessoa;
    }

    public void setPacientePessoa(String pacientePessoa) {
        this.pacientePessoa = pacientePessoa;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public TipoAgendamento getTipoAgendamento() {
        return tipoAgendamento;
    }

    public void setTipoAgendamento(TipoAgendamento tipoAgendamento) {
        this.tipoAgendamento = tipoAgendamento;
    }

    public TipoRecorrencia getTipoRecorrencia() {
        return tipoRecorrencia;
    }

    public void setTipoRecorrencia(TipoRecorrencia tipoRecorrencia) {
        this.tipoRecorrencia = tipoRecorrencia;
    }

    public Integer getQuantidadeRecorrencia() {
        return quantidadeRecorrencia;
    }

    public void setQuantidadeRecorrencia(Integer quantidadeRecorrencia) {
        this.quantidadeRecorrencia = quantidadeRecorrencia;
    }

    public String getGrupoRecorrencia() {
        return grupoRecorrencia;
    }

    public void setGrupoRecorrencia(String grupoRecorrencia) {
        this.grupoRecorrencia = grupoRecorrencia;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}