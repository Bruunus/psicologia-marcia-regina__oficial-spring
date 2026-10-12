package br.com.psicologia.marcia.service.finalizacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.DTO.finalizacao.FinalizarAtendimentoResponse;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.model.enums.StatusPaciente;
import br.com.psicologia.marcia.model.enums.StatusPagamento;
import br.com.psicologia.marcia.repository.financeiro.PagamentoPacienteRepository;
import br.com.psicologia.marcia.repository.paciente.UpdatePacienteRepository;

@Service
public class FinalizarAtendimentoService {

    @Autowired
    private UpdatePacienteRepository updatePacienteRepository;

    @Autowired
    private PagamentoPacienteRepository pagamentoPacienteRepository;

    @Transactional
    public FinalizarAtendimentoResponse finalizarAtendimento(
            Long pacienteId
    ) {

        Paciente paciente = updatePacienteRepository
                .findById(pacienteId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Paciente não encontrado."
                        )
                );

        if (paciente.getStatusPaciente() == StatusPaciente.ARQUIVADO) {
            throw new IllegalStateException(
                    "O atendimento deste paciente já foi finalizado."
            );
        }

        boolean possuiPagamentosPendentes =
                pagamentoPacienteRepository
                        .existsByPacienteIdAndStatusPagamentoAndStatusDelete(
                                pacienteId,
                                StatusPagamento.EM_ABERTO,
                                StatusDelete.NAO_DELETADO
                        );

        if (possuiPagamentosPendentes) {
            return new FinalizarAtendimentoResponse(
                    false,
                    true,
                    "Existem pagamentos pendentes do paciente. "
                            + "É necessário quitar todos os serviços "
                            + "antes de finalizar o atendimento."
            );
        }

        paciente.setStatusPaciente(StatusPaciente.ARQUIVADO);

        updatePacienteRepository.save(paciente);

        return new FinalizarAtendimentoResponse(
                true,
                false,
                "Atendimento finalizado com sucesso."
        );
    }
}