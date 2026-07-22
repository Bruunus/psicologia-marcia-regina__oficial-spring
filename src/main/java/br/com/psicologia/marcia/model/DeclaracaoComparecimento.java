package br.com.psicologia.marcia.model;

import java.time.LocalDate;
import java.time.LocalTime;

import br.com.psicologia.marcia.model.enums.OrientacaoDeclaracaoComparecimento;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "declaracao_comparecimento")
public class DeclaracaoComparecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(name = "data_comparecimento", nullable = false)
    private LocalDate dataComparecimento;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_termino")
    private LocalTime horaTermino;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "orientacao",
            nullable = false,
            columnDefinition = "ENUM('RETORNAR_AO_TRABALHO', 'PERMANECER_EM_REPOUSO')"
    )
    private OrientacaoDeclaracaoComparecimento orientacao;

    @Column(name = "quantidade_dias_repouso")
    private Integer quantidadeDiasRepouso;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status_delete",
            nullable = false,
            columnDefinition = "ENUM('NAO_DELETADO', 'DELETADO')"
    )
    private StatusDelete statusDelete;

    @PrePersist
    public void prePersist() {
        if (this.statusDelete == null) {
            this.statusDelete = StatusDelete.NAO_DELETADO;
        }
    }

    public Long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
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

    public StatusDelete getStatusDelete() {
        return statusDelete;
    }

    public void setStatusDelete(StatusDelete statusDelete) {
        this.statusDelete = statusDelete;
    }
}