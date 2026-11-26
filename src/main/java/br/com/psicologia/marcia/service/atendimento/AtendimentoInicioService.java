package br.com.psicologia.marcia.service.atendimento;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.repository.paciente.UpdatePacienteRepository;

@Service
public class AtendimentoInicioService {

    private final UpdatePacienteRepository pacienteRepository;

    public AtendimentoInicioService(
            UpdatePacienteRepository pacienteRepository
    ) {
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void registrarInicio(Long pacienteId) {

        if (pacienteId == null || pacienteId <= 0) {
            throw new IllegalArgumentException(
                    "Identificador do paciente inválido."
            );
        }

        Paciente paciente = pacienteRepository
                .findById(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Paciente não encontrado."
                ));

        if (!paciente.isAtendimentoIniciado()) {
            paciente.setAtendimentoIniciado(true);
        }
    }
}
