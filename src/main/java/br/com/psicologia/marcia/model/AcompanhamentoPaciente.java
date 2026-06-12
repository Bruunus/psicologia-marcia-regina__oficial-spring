package br.com.psicologia.marcia.model;

import java.time.LocalDate;

import br.com.psicologia.marcia.model.enums.StatusDelete;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "acompanhamento_paciente")
public class AcompanhamentoPaciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "sigilo_etico", columnDefinition = "LONGTEXT")
    private String sigiloEtico;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "acompanhamento", columnDefinition = "LONGTEXT")
    private String acompanhamento;

    @Column(name = "data_acompanhamento", nullable = false)
    private LocalDate dataAcompanhamento;

    @Column(name = "paciente_ausente", nullable = false)
    private Boolean pacienteAusente = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_delete", nullable = false)
    private StatusDelete statusDelete = StatusDelete.NAO_DELETADO;

    public AcompanhamentoPaciente() {
    }

    public AcompanhamentoPaciente(
            Paciente paciente,
            String sigiloEtico,
            String acompanhamento,
            LocalDate dataAcompanhamento
    ) {
        this.paciente = paciente;
        this.sigiloEtico = sigiloEtico;
        this.acompanhamento = acompanhamento;
        this.dataAcompanhamento = dataAcompanhamento;
        this.pacienteAusente = false;
        this.statusDelete = StatusDelete.NAO_DELETADO;
    }

    public Long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public String getSigiloEtico() {
        return sigiloEtico;
    }

    public String getAcompanhamento() {
        return acompanhamento;
    }

    public LocalDate getDataAcompanhamento() {
        return dataAcompanhamento;
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

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public void setSigiloEtico(String sigiloEtico) {
        this.sigiloEtico = sigiloEtico;
    }

    public void setAcompanhamento(String acompanhamento) {
        this.acompanhamento = acompanhamento;
    }

    public void setDataAcompanhamento(LocalDate dataAcompanhamento) {
        this.dataAcompanhamento = dataAcompanhamento;
    }

    public void setPacienteAusente(Boolean pacienteAusente) {
        this.pacienteAusente = pacienteAusente;
    }

    public void setStatusDelete(StatusDelete statusDelete) {
        this.statusDelete = statusDelete;
    }
}