package br.com.psicologia.marcia.DTO.acompanhamento;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import br.com.psicologia.marcia.model.AcompanhamentoPaciente;
import br.com.psicologia.marcia.model.enums.StatusDelete;

public class AcompanhamentoPacienteResponseDTO {

    private Long id;
    private Long pacienteId;
    private String sigiloEtico;
    private String acompanhamento;
    private LocalDateTime dataAcompanhamento;
    private String dataAcompanhamentoFormatada;
    private String horaAcompanhamentoFormatada;
    private String dataHoraAcompanhamentoFormatada;
    private Boolean pacienteAusente;
    private StatusDelete statusDelete;

    public AcompanhamentoPacienteResponseDTO() {
    }

    public AcompanhamentoPacienteResponseDTO(AcompanhamentoPaciente acompanhamentoPaciente) {
        this.id = acompanhamentoPaciente.getId();
        this.pacienteId = acompanhamentoPaciente.getPaciente().getId();
        this.sigiloEtico = acompanhamentoPaciente.getSigiloEtico();
        this.acompanhamento = acompanhamentoPaciente.getAcompanhamento();
        this.dataAcompanhamento = acompanhamentoPaciente.getDataAcompanhamento();
        this.dataAcompanhamentoFormatada = formatarData(acompanhamentoPaciente.getDataAcompanhamento());
        this.horaAcompanhamentoFormatada = formatarHora(acompanhamentoPaciente.getDataAcompanhamento());
        this.dataHoraAcompanhamentoFormatada = formatarDataHora(acompanhamentoPaciente.getDataAcompanhamento());
        this.pacienteAusente = acompanhamentoPaciente.getPacienteAusente();
        this.statusDelete = acompanhamentoPaciente.getStatusDelete();
    }

    private String formatarData(LocalDateTime data) {
        if (data == null) {
            return null;
        }

        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String formatarHora(LocalDateTime data) {
        if (data == null) {
            return null;
        }

        return data.format(DateTimeFormatter.ofPattern("HH:mm'hs'"));
    }

    private String formatarDataHora(LocalDateTime data) {
        if (data == null) {
            return null;
        }

        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm'hs'"));
    }

    public Long getId() {
        return id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public String getSigiloEtico() {
        return sigiloEtico;
    }

    public String getAcompanhamento() {
        return acompanhamento;
    }

    public LocalDateTime getDataAcompanhamento() {
        return dataAcompanhamento;
    }

    public String getDataAcompanhamentoFormatada() {
        return dataAcompanhamentoFormatada;
    }

    public String getHoraAcompanhamentoFormatada() {
        return horaAcompanhamentoFormatada;
    }

    public String getDataHoraAcompanhamentoFormatada() {
        return dataHoraAcompanhamentoFormatada;
    }

    public Boolean getPacienteAusente() {
        return pacienteAusente;
    }

    public StatusDelete getStatusDelete() {
        return statusDelete;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public void setSigiloEtico(String sigiloEtico) {
        this.sigiloEtico = sigiloEtico;
    }

    public void setAcompanhamento(String acompanhamento) {
        this.acompanhamento = acompanhamento;
    }

    public void setDataAcompanhamento(LocalDateTime dataAcompanhamento) {
        this.dataAcompanhamento = dataAcompanhamento;
    }

    public void setDataAcompanhamentoFormatada(String dataAcompanhamentoFormatada) {
        this.dataAcompanhamentoFormatada = dataAcompanhamentoFormatada;
    }

    public void setHoraAcompanhamentoFormatada(String horaAcompanhamentoFormatada) {
        this.horaAcompanhamentoFormatada = horaAcompanhamentoFormatada;
    }

    public void setDataHoraAcompanhamentoFormatada(String dataHoraAcompanhamentoFormatada) {
        this.dataHoraAcompanhamentoFormatada = dataHoraAcompanhamentoFormatada;
    }

    public void setPacienteAusente(Boolean pacienteAusente) {
        this.pacienteAusente = pacienteAusente;
    }

    public void setStatusDelete(StatusDelete statusDelete) {
        this.statusDelete = statusDelete;
    }
}