package br.com.psicologia.marcia.DTO.agenda;

import java.time.LocalDate;
import java.util.List;

public class ConflitoAgendamentoResponse {

    private boolean conflito;
    private String mensagem;
    private int quantidadeSolicitada;
    private int quantidadeDatasConflitantes;
    private int quantidadeDatasDisponiveis;
    private List<LocalDate> datasConflitantes;
    private List<AgendamentoResponse>
        agendamentosConflitantes;

    public ConflitoAgendamentoResponse() {
    }

    public ConflitoAgendamentoResponse(
        boolean conflito,
        String mensagem,
        List<AgendamentoResponse>
            agendamentosConflitantes
    ) {
        this.conflito = conflito;
        this.mensagem = mensagem;
        this.agendamentosConflitantes =
            agendamentosConflitantes;

        this.datasConflitantes =
            agendamentosConflitantes == null
                ? List.of()
                : agendamentosConflitantes
                    .stream()
                    .map(
                        AgendamentoResponse
                            ::getDataAgendamento
                    )
                    .distinct()
                    .sorted()
                    .toList();

        this.quantidadeSolicitada = 1;

        this.quantidadeDatasConflitantes =
            this.datasConflitantes.isEmpty()
                ? 0
                : 1;

        this.quantidadeDatasDisponiveis =
            this.quantidadeDatasConflitantes == 0
                ? 1
                : 0;
    }

    public ConflitoAgendamentoResponse(
        boolean conflito,
        String mensagem,
        int quantidadeSolicitada,
        int quantidadeDatasConflitantes,
        int quantidadeDatasDisponiveis,
        List<LocalDate> datasConflitantes,
        List<AgendamentoResponse>
            agendamentosConflitantes
    ) {
        this.conflito = conflito;
        this.mensagem = mensagem;
        this.quantidadeSolicitada =
            quantidadeSolicitada;
        this.quantidadeDatasConflitantes =
            quantidadeDatasConflitantes;
        this.quantidadeDatasDisponiveis =
            quantidadeDatasDisponiveis;
        this.datasConflitantes =
            datasConflitantes;
        this.agendamentosConflitantes =
            agendamentosConflitantes;
    }

    public boolean isConflito() {
        return conflito;
    }

    public void setConflito(boolean conflito) {
        this.conflito = conflito;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
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

    public int getQuantidadeDatasConflitantes() {
        return quantidadeDatasConflitantes;
    }

    public void setQuantidadeDatasConflitantes(
        int quantidadeDatasConflitantes
    ) {
        this.quantidadeDatasConflitantes =
            quantidadeDatasConflitantes;
    }

    public int getQuantidadeDatasDisponiveis() {
        return quantidadeDatasDisponiveis;
    }

    public void setQuantidadeDatasDisponiveis(
        int quantidadeDatasDisponiveis
    ) {
        this.quantidadeDatasDisponiveis =
            quantidadeDatasDisponiveis;
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