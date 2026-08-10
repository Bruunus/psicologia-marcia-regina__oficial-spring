package br.com.psicologia.marcia.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "laudo")
public class Laudo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "paciente_id",
        nullable = false
    )
    private Paciente paciente;

    @Column(
        name = "dominancia_manual",
        length = 255
    )
    private String dominanciaManual;

    @Column(
        name = "periodo_avaliacao_inicio"
    )
    private LocalDate periodoAvaliacaoInicio;

    @Column(
        name = "periodo_avaliacao_fim"
    )
    private LocalDate periodoAvaliacaoFim;

    @Column(
        name = "encaminhamento",
        length = 255
    )
    private String encaminhamento;

    @Column(
        name = "queixa",
        columnDefinition = "TEXT"
    )
    private String queixa;

    @Column(
        name = "quantidade_sessoes"
    )
    private Integer quantidadeSessoes;

    @Column(
        name = "periodo",
        length = 255
    )
    private String periodo;

    @Column(
        name = "duracao_minutos"
    )
    private Integer duracaoMinutos;

    @Column(
        name = "testes_psicologicos",
        columnDefinition = "TEXT"
    )
    private String testesPsicologicos;

    @Column(
        name = "tarefas_estudos",
        columnDefinition = "TEXT"
    )
    private String tarefasEstudos;

    @Column(
        name = "escalas_questionarios",
        columnDefinition = "TEXT"
    )
    private String escalasQuestionarios;

    @Column(
        name = "dados_anamnese",
        columnDefinition = "TEXT"
    )
    private String dadosAnamnese;

    @Column(
        name = "dados_atuais",
        columnDefinition = "TEXT"
    )
    private String dadosAtuais;

    @Column(
        name = "observacoes_clinicas",
        columnDefinition = "TEXT"
    )
    private String observacoesClinicas;

    @Column(
        name = "eficiencia_intelectual",
        columnDefinition = "TEXT"
    )
    private String eficienciaIntelectual;

    @Column(
        name = "conclusao",
        columnDefinition = "TEXT"
    )
    private String conclusao;

    @Column(
        name = "incluir_eficiencia_intelectual",
        nullable = false
    )
    private Boolean incluirEficienciaIntelectual = false;

    @Column(
        name = "incluir_escala_wasi",
        nullable = false
    )
    private Boolean incluirEscalaWasi = false;

    @Column(
        name = "incluir_funcoes_executivas_atencionais",
        nullable = false
    )
    private Boolean incluirFuncoesExecutivasAtencionais = false;

    @Column(
        name = "incluir_memoria_visual_auditivo_verbal",
        nullable = false
    )
    private Boolean incluirMemoriaVisualAuditivoVerbal = false;

    @Column(
        name = "incluir_memoria_aprendizagem",
        nullable = false
    )
    private Boolean incluirMemoriaAprendizagem = false;

    @Column(
        name = "incluir_funcoes_visoespaciais",
        nullable = false
    )
    private Boolean incluirFuncoesVisoespaciais = false;

    @Column(
        name = "incluir_escala_srs2",
        nullable = false
    )
    private Boolean incluirEscalaSrs2 = false;

    @Column(
        name = "data_criacao",
        nullable = false,
        updatable = false
    )
    private LocalDateTime dataCriacao;

    @Column(
        name = "data_atualizacao"
    )
    private LocalDateTime dataAtualizacao;

    @Column(
        name = "status_delete",
        nullable = false
    )
    private Boolean statusDelete = false;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();

        this.dataCriacao = agora;
        this.dataAtualizacao = agora;

        normalizarCamposBooleanos();
    }

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();

        normalizarCamposBooleanos();
    }

    private void normalizarCamposBooleanos() {
        if (this.incluirEficienciaIntelectual == null) {
            this.incluirEficienciaIntelectual = false;
        }

        if (this.incluirEscalaWasi == null) {
            this.incluirEscalaWasi = false;
        }

        if (this.incluirFuncoesExecutivasAtencionais == null) {
            this.incluirFuncoesExecutivasAtencionais = false;
        }

        if (this.incluirMemoriaVisualAuditivoVerbal == null) {
            this.incluirMemoriaVisualAuditivoVerbal = false;
        }

        if (this.incluirMemoriaAprendizagem == null) {
            this.incluirMemoriaAprendizagem = false;
        }

        if (this.incluirFuncoesVisoespaciais == null) {
            this.incluirFuncoesVisoespaciais = false;
        }

        if (this.incluirEscalaSrs2 == null) {
            this.incluirEscalaSrs2 = false;
        }

        if (this.statusDelete == null) {
            this.statusDelete = false;
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

    public String getDominanciaManual() {
        return dominanciaManual;
    }

    public void setDominanciaManual(String dominanciaManual) {
        this.dominanciaManual = dominanciaManual;
    }

    public LocalDate getPeriodoAvaliacaoInicio() {
        return periodoAvaliacaoInicio;
    }

    public void setPeriodoAvaliacaoInicio(
        LocalDate periodoAvaliacaoInicio
    ) {
        this.periodoAvaliacaoInicio = periodoAvaliacaoInicio;
    }

    public LocalDate getPeriodoAvaliacaoFim() {
        return periodoAvaliacaoFim;
    }

    public void setPeriodoAvaliacaoFim(
        LocalDate periodoAvaliacaoFim
    ) {
        this.periodoAvaliacaoFim = periodoAvaliacaoFim;
    }

    public String getEncaminhamento() {
        return encaminhamento;
    }

    public void setEncaminhamento(String encaminhamento) {
        this.encaminhamento = encaminhamento;
    }

    public String getQueixa() {
        return queixa;
    }

    public void setQueixa(String queixa) {
        this.queixa = queixa;
    }

    public Integer getQuantidadeSessoes() {
        return quantidadeSessoes;
    }

    public void setQuantidadeSessoes(Integer quantidadeSessoes) {
        this.quantidadeSessoes = quantidadeSessoes;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public String getTestesPsicologicos() {
        return testesPsicologicos;
    }

    public void setTestesPsicologicos(String testesPsicologicos) {
        this.testesPsicologicos = testesPsicologicos;
    }

    public String getTarefasEstudos() {
        return tarefasEstudos;
    }

    public void setTarefasEstudos(String tarefasEstudos) {
        this.tarefasEstudos = tarefasEstudos;
    }

    public String getEscalasQuestionarios() {
        return escalasQuestionarios;
    }

    public void setEscalasQuestionarios(
        String escalasQuestionarios
    ) {
        this.escalasQuestionarios = escalasQuestionarios;
    }

    public String getDadosAnamnese() {
        return dadosAnamnese;
    }

    public void setDadosAnamnese(String dadosAnamnese) {
        this.dadosAnamnese = dadosAnamnese;
    }

    public String getDadosAtuais() {
        return dadosAtuais;
    }

    public void setDadosAtuais(String dadosAtuais) {
        this.dadosAtuais = dadosAtuais;
    }

    public String getObservacoesClinicas() {
        return observacoesClinicas;
    }

    public void setObservacoesClinicas(
        String observacoesClinicas
    ) {
        this.observacoesClinicas = observacoesClinicas;
    }

    public String getEficienciaIntelectual() {
        return eficienciaIntelectual;
    }

    public void setEficienciaIntelectual(
        String eficienciaIntelectual
    ) {
        this.eficienciaIntelectual = eficienciaIntelectual;
    }

    public String getConclusao() {
        return conclusao;
    }

    public void setConclusao(String conclusao) {
        this.conclusao = conclusao;
    }

    public Boolean getIncluirEficienciaIntelectual() {
        return incluirEficienciaIntelectual;
    }

    public void setIncluirEficienciaIntelectual(
        Boolean incluirEficienciaIntelectual
    ) {
        this.incluirEficienciaIntelectual =
            incluirEficienciaIntelectual;
    }

    public Boolean getIncluirEscalaWasi() {
        return incluirEscalaWasi;
    }

    public void setIncluirEscalaWasi(
        Boolean incluirEscalaWasi
    ) {
        this.incluirEscalaWasi = incluirEscalaWasi;
    }

    public Boolean getIncluirFuncoesExecutivasAtencionais() {
        return incluirFuncoesExecutivasAtencionais;
    }

    public void setIncluirFuncoesExecutivasAtencionais(
        Boolean incluirFuncoesExecutivasAtencionais
    ) {
        this.incluirFuncoesExecutivasAtencionais =
            incluirFuncoesExecutivasAtencionais;
    }

    public Boolean getIncluirMemoriaVisualAuditivoVerbal() {
        return incluirMemoriaVisualAuditivoVerbal;
    }

    public void setIncluirMemoriaVisualAuditivoVerbal(
        Boolean incluirMemoriaVisualAuditivoVerbal
    ) {
        this.incluirMemoriaVisualAuditivoVerbal =
            incluirMemoriaVisualAuditivoVerbal;
    }

    public Boolean getIncluirMemoriaAprendizagem() {
        return incluirMemoriaAprendizagem;
    }

    public void setIncluirMemoriaAprendizagem(
        Boolean incluirMemoriaAprendizagem
    ) {
        this.incluirMemoriaAprendizagem =
            incluirMemoriaAprendizagem;
    }

    public Boolean getIncluirFuncoesVisoespaciais() {
        return incluirFuncoesVisoespaciais;
    }

    public void setIncluirFuncoesVisoespaciais(
        Boolean incluirFuncoesVisoespaciais
    ) {
        this.incluirFuncoesVisoespaciais =
            incluirFuncoesVisoespaciais;
    }

    public Boolean getIncluirEscalaSrs2() {
        return incluirEscalaSrs2;
    }

    public void setIncluirEscalaSrs2(
        Boolean incluirEscalaSrs2
    ) {
        this.incluirEscalaSrs2 = incluirEscalaSrs2;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(
        LocalDateTime dataAtualizacao
    ) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Boolean getStatusDelete() {
        return statusDelete;
    }

    public void setStatusDelete(Boolean statusDelete) {
        this.statusDelete = statusDelete;
    }
}