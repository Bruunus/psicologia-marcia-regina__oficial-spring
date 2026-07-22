package br.com.psicologia.marcia.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "relatorio_psicologico")
public class RelatorioPsicologico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mesAcompanhamento;

    private Integer anoAcompanhamento;

    private String abordagem;

    private Integer ocorrencia;

    private String formato;

    @Column(columnDefinition = "TEXT")
    private String analise;

    private String cidadeEmissao;

    private Integer diaEmissao;

    private String mesEmissao;

    private Integer anoEmissao;

    private LocalDate dataEmissaoSistema;

    private LocalDateTime dataCriacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @OneToMany(mappedBy = "relatorioPsicologico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RelatorioPsicologicoHipoteseDiagnostica> hipotesesDiagnosticas = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();

        if (this.dataEmissaoSistema == null) {
            this.dataEmissaoSistema = LocalDate.now();
        }
    }

    public void adicionarHipoteseDiagnostica(RelatorioPsicologicoHipoteseDiagnostica hipoteseDiagnostica) {
        hipoteseDiagnostica.setRelatorioPsicologico(this);
        this.hipotesesDiagnosticas.add(hipoteseDiagnostica);
    }

    public Long getId() {
        return id;
    }

    public String getMesAcompanhamento() {
        return mesAcompanhamento;
    }

    public void setMesAcompanhamento(String mesAcompanhamento) {
        this.mesAcompanhamento = mesAcompanhamento;
    }

    public Integer getAnoAcompanhamento() {
        return anoAcompanhamento;
    }

    public void setAnoAcompanhamento(Integer anoAcompanhamento) {
        this.anoAcompanhamento = anoAcompanhamento;
    }

    public String getAbordagem() {
        return abordagem;
    }

    public void setAbordagem(String abordagem) {
        this.abordagem = abordagem;
    }

    public Integer getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(Integer ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getAnalise() {
        return analise;
    }

    public void setAnalise(String analise) {
        this.analise = analise;
    }

    public String getCidadeEmissao() {
        return cidadeEmissao;
    }

    public void setCidadeEmissao(String cidadeEmissao) {
        this.cidadeEmissao = cidadeEmissao;
    }

    public Integer getDiaEmissao() {
        return diaEmissao;
    }

    public void setDiaEmissao(Integer diaEmissao) {
        this.diaEmissao = diaEmissao;
    }

    public String getMesEmissao() {
        return mesEmissao;
    }

    public void setMesEmissao(String mesEmissao) {
        this.mesEmissao = mesEmissao;
    }

    public Integer getAnoEmissao() {
        return anoEmissao;
    }

    public void setAnoEmissao(Integer anoEmissao) {
        this.anoEmissao = anoEmissao;
    }

    public LocalDate getDataEmissaoSistema() {
        return dataEmissaoSistema;
    }

    public void setDataEmissaoSistema(LocalDate dataEmissaoSistema) {
        this.dataEmissaoSistema = dataEmissaoSistema;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public List<RelatorioPsicologicoHipoteseDiagnostica> getHipotesesDiagnosticas() {
        return hipotesesDiagnosticas;
    }

    public void setHipotesesDiagnosticas(List<RelatorioPsicologicoHipoteseDiagnostica> hipotesesDiagnosticas) {
        this.hipotesesDiagnosticas = hipotesesDiagnosticas;
    }
}