package br.com.psicologia.marcia.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import br.com.psicologia.marcia.model.enums.TipoAgendamento;
import br.com.psicologia.marcia.model.enums.TipoRecorrencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "agendamento",
    indexes = {
        @Index(
            name = "idx_agendamento_data",
            columnList = "data_agendamento"
        ),
        @Index(
            name = "idx_agendamento_data_horario",
            columnList = "data_agendamento, horario"
        ),
        @Index(
            name = "idx_agendamento_tipo",
            columnList = "tipo_agendamento"
        ),
        @Index(
            name = "idx_agendamento_grupo_recorrencia",
            columnList = "grupo_recorrencia"
        )
    }
)
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = true
    )
    @JoinColumn(
        name = "paciente_id",
        referencedColumnName = "id",
        nullable = true
    )
    private Paciente paciente;

    @Column(
        name = "nome_pessoa",
        length = 150
    )
    private String nomePessoa;

    @Column(
        name = "data_agendamento",
        nullable = false
    )
    private LocalDate dataAgendamento;

    @Column(
        name = "horario",
        nullable = false
    )
    private LocalTime horario;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "tipo_agendamento",
        nullable = false,
        length = 30
    )
    private TipoAgendamento tipoAgendamento;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "tipo_recorrencia",
        nullable = false,
        length = 30
    )
    private TipoRecorrencia tipoRecorrencia;

    @Column(
        name = "grupo_recorrencia",
        length = 36
    )
    private String grupoRecorrencia;

    @Column(
        name = "data_criacao",
        nullable = false,
        updatable = false
    )
    private LocalDateTime dataCriacao;
    
    @Column(name = "quantidade_recorrencia")
    private Integer quantidadeRecorrencia;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }

        if (tipoRecorrencia == null) {
            tipoRecorrencia = TipoRecorrencia.SEM_RECORRENCIA;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
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

	public Integer getQuantidadeRecorrencia() {
		return quantidadeRecorrencia;
	}

	public void setQuantidadeRecorrencia(Integer quantidadeRecorrencia) {
		this.quantidadeRecorrencia = quantidadeRecorrencia;
	}
}