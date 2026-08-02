package br.com.psicologia.marcia.DTO.agenda;

import java.time.LocalDate;
import java.time.LocalTime;

import br.com.psicologia.marcia.model.enums.AcaoConflito;
import br.com.psicologia.marcia.model.enums.TipoRecorrencia;

public class AgendamentoConsultaRequest {

    private Long pacienteId;
    private LocalDate dataAgendamento;
    private LocalTime horario;
    private TipoRecorrencia tipoRecorrencia;
    private Integer quantidadeRecorrencia;
    private AcaoConflito acaoConflito;

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(
        LocalDate dataAgendamento
    ) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public TipoRecorrencia getTipoRecorrencia() {
        return tipoRecorrencia;
    }

    public void setTipoRecorrencia(
        TipoRecorrencia tipoRecorrencia
    ) {
        this.tipoRecorrencia = tipoRecorrencia;
    }

    public Integer getQuantidadeRecorrencia() {
        return quantidadeRecorrencia;
    }

    public void setQuantidadeRecorrencia(
        Integer quantidadeRecorrencia
    ) {
        this.quantidadeRecorrencia =
            quantidadeRecorrencia;
    }

    public AcaoConflito getAcaoConflito() {
        return acaoConflito;
    }

    public void setAcaoConflito(
        AcaoConflito acaoConflito
    ) {
        this.acaoConflito = acaoConflito;
    }
}