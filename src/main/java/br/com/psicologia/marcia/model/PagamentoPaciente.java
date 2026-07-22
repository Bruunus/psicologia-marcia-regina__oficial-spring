package br.com.psicologia.marcia.model;

import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.model.enums.StatusPagamento;
import br.com.psicologia.marcia.model.enums.TipoDePagamento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagamento_paciente")
public class PagamentoPaciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Relacionamento com paciente.
     * Não é necessário alterar a entity Paciente nesta fase.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(name = "quantidade_sessao", nullable = false)
    private Integer quantidadeSessao;

    @Column(name = "valor_sessao", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorSessao;

    @Column(name = "dt_do_atendimento", nullable = false)
    private LocalDate dtDoAtendimento;

    @Column(name = "dt_pag_previsto", nullable = false)
    private LocalDate dtPagPrevisto;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo_de_pagamento",
            nullable = false,
            columnDefinition = "ENUM('AVULSO','MENSAL')"
    )
    private TipoDePagamento tipoDePagamento;

    /*
     * Esse status é o status real salvo no banco.
     *
     * EM_ABERTO:
     * O backend calcula dinamicamente para a tela:
     * - Á cobrar
     * - Hoje
     * - Pendente
     *
     * PAGO:
     * Trava o pagamento como pago e não aplica mais a regra dinâmica de data.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "status_pagamento",
            nullable = false,
            columnDefinition = "ENUM('EM_ABERTO','PAGO')"
    )
    private StatusPagamento statusPagamento = StatusPagamento.EM_ABERTO;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status_delete",
            nullable = false,
            columnDefinition = "ENUM('DELETADO','NAO_DELETADO')"
    )
    private StatusDelete statusDelete = StatusDelete.NAO_DELETADO;

    public PagamentoPaciente() {
    }

    @PrePersist
    public void prePersist() {
        if (this.statusPagamento == null) {
            this.statusPagamento = StatusPagamento.EM_ABERTO;
        }

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

    public Integer getQuantidadeSessao() {
        return quantidadeSessao;
    }

    public void setQuantidadeSessao(Integer quantidadeSessao) {
        this.quantidadeSessao = quantidadeSessao;
    }

    public BigDecimal getValorSessao() {
        return valorSessao;
    }

    public void setValorSessao(BigDecimal valorSessao) {
        this.valorSessao = valorSessao;
    }

    public LocalDate getDtDoAtendimento() {
        return dtDoAtendimento;
    }

    public void setDtDoAtendimento(LocalDate dtDoAtendimento) {
        this.dtDoAtendimento = dtDoAtendimento;
    }

    public LocalDate getDtPagPrevisto() {
        return dtPagPrevisto;
    }

    public void setDtPagPrevisto(LocalDate dtPagPrevisto) {
        this.dtPagPrevisto = dtPagPrevisto;
    }

    public TipoDePagamento getTipoDePagamento() {
        return tipoDePagamento;
    }

    public void setTipoDePagamento(TipoDePagamento tipoDePagamento) {
        this.tipoDePagamento = tipoDePagamento;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public StatusDelete getStatusDelete() {
        return statusDelete;
    }

    public void setStatusDelete(StatusDelete statusDelete) {
        this.statusDelete = statusDelete;
    }
}