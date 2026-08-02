package br.com.psicologia.marcia.DTO.agenda;

import java.time.LocalDate;
import java.util.List;

import br.com.psicologia.marcia.model.enums.AcaoConflito;

public class ResultadoAgendamentoConsultaResponse {

    private boolean bloqueadoPorConflito;
    private String mensagem;
    private AcaoConflito acaoConflito;
    private int quantidadeSolicitada;
    private int quantidadeSalva;
    private int quantidadeIgnorada;
    private List<LocalDate> datasConflitantes;
    private List<AgendamentoResponse> agendamentosSalvos;
    private List<AgendamentoResponse>
        agendamentosConflitantes;

    public boolean isBloqueadoPorConflito() {
        return bloqueadoPorConflito;
    }

    public void setBloqueadoPorConflito(
        boolean bloqueadoPorConflito
    ) {
        this.bloqueadoPorConflito =
            bloqueadoPorConflito;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public AcaoConflito getAcaoConflito() {
        return acaoConflito;
    }

    public void setAcaoConflito(
        AcaoConflito acaoConflito
    ) {
        this.acaoConflito = acaoConflito;
    }

    public int getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }

    public void setQuantidadeSolicitada(
        int quantidadeSolicitada
    ) {
        this.quantidadeSolicitada =
            quantidadeSolicitada;
    }

    public int getQuantidadeSalva() {
        return quantidadeSalva;
    }

    public void setQuantidadeSalva(
        int quantidadeSalva
    ) {
        this.quantidadeSalva = quantidadeSalva;
    }

    public int getQuantidadeIgnorada() {
        return quantidadeIgnorada;
    }

    public void setQuantidadeIgnorada(
        int quantidadeIgnorada
    ) {
        this.quantidadeIgnorada =
            quantidadeIgnorada;
    }

    public List<LocalDate> getDatasConflitantes() {
        return datasConflitantes;
    }

    public void setDatasConflitantes(
        List<LocalDate> datasConflitantes
    ) {
        this.datasConflitantes =
            datasConflitantes;
    }

    public List<AgendamentoResponse>
        getAgendamentosSalvos() {
        return agendamentosSalvos;
    }

    public void setAgendamentosSalvos(
        List<AgendamentoResponse>
            agendamentosSalvos
    ) {
        this.agendamentosSalvos =
            agendamentosSalvos;
    }

    public List<AgendamentoResponse>
        getAgendamentosConflitantes() {
        return agendamentosConflitantes;
    }

    public void setAgendamentosConflitantes(
        List<AgendamentoResponse>
            agendamentosConflitantes
    ) {
        this.agendamentosConflitantes =
            agendamentosConflitantes;
    }
}