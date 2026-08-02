package br.com.psicologia.marcia.config;

import java.time.LocalDate;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.psicologia.marcia.service.agenda.AgendamentoService;

@Component
public class AgendamentoLimpezaScheduler {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            AgendamentoLimpezaScheduler.class
        );

    private static final ZoneId FUSO_HORARIO =
        ZoneId.of("America/Sao_Paulo");

    private final AgendamentoService
        agendamentoService;

    public AgendamentoLimpezaScheduler(
        AgendamentoService agendamentoService
    ) {
        this.agendamentoService =
            agendamentoService;
    }

    @Scheduled(
        cron = "${agenda.limpeza.cron:0 0 3 * * *}",
        zone = "America/Sao_Paulo"
    )
    public void executarLimpezaAutomatica() {
        LocalDate dataAtual =
            LocalDate.now(FUSO_HORARIO);

        LocalDate dataLimite =
            dataAtual.minusMonths(5);

        long quantidadeExcluida =
            agendamentoService
                .excluirAgendamentosAnterioresA(
                    dataLimite
                );

        LOGGER.info(
            "Limpeza automática da Agenda concluída. "
                + "Data limite: {}. "
                + "Quantidade excluída: {}.",
            dataLimite,
            quantidadeExcluida
        );
    }
}