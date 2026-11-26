package br.com.psicologia.marcia.service.atendimento;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AtendimentoHistoricoInitializer implements ApplicationRunner {

    private final AtendimentoHistoricoService atendimentoHistoricoService;

    public AtendimentoHistoricoInitializer(
            AtendimentoHistoricoService atendimentoHistoricoService
    ) {
        this.atendimentoHistoricoService = atendimentoHistoricoService;
    }

    @Override
    public void run(ApplicationArguments args) {
        atendimentoHistoricoService.atualizarPacientesAntigos();
    }
}
