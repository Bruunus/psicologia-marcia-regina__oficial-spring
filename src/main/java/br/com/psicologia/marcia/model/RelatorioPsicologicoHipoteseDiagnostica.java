package br.com.psicologia.marcia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "relatorio_psicologico_hipotese_diagnostica")
public class RelatorioPsicologicoHipoteseDiagnostica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relatorio_psicologico_id", nullable = false)
    private RelatorioPsicologico relatorioPsicologico;

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public RelatorioPsicologico getRelatorioPsicologico() {
        return relatorioPsicologico;
    }

    public void setRelatorioPsicologico(RelatorioPsicologico relatorioPsicologico) {
        this.relatorioPsicologico = relatorioPsicologico;
    }
}