package br.com.psicologia.marcia.service.faturamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.DTO.faturamento.ListarMeuFaturamentoRequest;
import br.com.psicologia.marcia.DTO.faturamento.MeuFaturamentoPacienteResponse;
import br.com.psicologia.marcia.DTO.faturamento.MeuFaturamentoResponse;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.model.enums.StatusPagamento;
import br.com.psicologia.marcia.repository.acompanhamento.AcompanhamentoPacienteRepository;
import br.com.psicologia.marcia.repository.financeiro.PagamentoPacienteRepository;
import br.com.psicologia.marcia.repository.paciente.ReadPacienteRepository;
import br.com.psicologia.marcia.repository.relatorio.psicologico.RelatorioPsicologicoRepository;

@Service
public class MeuFaturamentoService {

    private final ReadPacienteRepository readPacienteRepository;
    private final PagamentoPacienteRepository pagamentoPacienteRepository;
    private final AcompanhamentoPacienteRepository acompanhamentoPacienteRepository;
    private final RelatorioPsicologicoRepository relatorioPsicologicoRepository;

    public MeuFaturamentoService(
            ReadPacienteRepository readPacienteRepository,
            PagamentoPacienteRepository pagamentoPacienteRepository,
            AcompanhamentoPacienteRepository acompanhamentoPacienteRepository,
            RelatorioPsicologicoRepository relatorioPsicologicoRepository
    ) {
        this.readPacienteRepository = readPacienteRepository;
        this.pagamentoPacienteRepository = pagamentoPacienteRepository;
        this.acompanhamentoPacienteRepository =
                acompanhamentoPacienteRepository;
        this.relatorioPsicologicoRepository =
                relatorioPsicologicoRepository;
    }

    @Transactional(readOnly = true)
    public MeuFaturamentoResponse listar(
            ListarMeuFaturamentoRequest request
    ) {
        String nomePaciente = request == null
                ? ""
                : normalizarNome(request.getNomePaciente());

        List<Paciente> pacientes;

        if (nomePaciente.isEmpty()) {
            pacientes = readPacienteRepository
                    .findAllByOrderByNomeCompletoAsc();
        } else {
            pacientes = readPacienteRepository
                    .findByNomeCompletoContainingIgnoreCaseOrderByNomeCompletoAsc(
                            nomePaciente
                    );
        }

        List<MeuFaturamentoPacienteResponse> linhas =
                new ArrayList<>();

        long totalConsultas = 0L;
        long totalAcompanhamentos = 0L;
        long totalRelatorios = 0L;
        BigDecimal rendimentoTotal = BigDecimal.ZERO;

        for (Paciente paciente : pacientes) {
            Long consultasRealizadas =
                    pagamentoPacienteRepository.somarConsultasRealizadas(
                            paciente.getId(),
                            StatusDelete.NAO_DELETADO
                    );

            Long acompanhamentosRealizados =
                    acompanhamentoPacienteRepository
                            .countByPacienteIdAndPacienteAusenteFalseAndStatusDelete(
                                    paciente.getId(),
                                    StatusDelete.NAO_DELETADO
                            );

            Long relatoriosGerados =
                    relatorioPsicologicoRepository.countByPacienteId(
                            paciente.getId()
                    );

            BigDecimal rendimento =
                    pagamentoPacienteRepository.somarRendimentoPago(
                            paciente.getId(),
                            StatusPagamento.PAGO,
                            StatusDelete.NAO_DELETADO
                    );

            consultasRealizadas = valorOuZero(consultasRealizadas);
            acompanhamentosRealizados =
                    valorOuZero(acompanhamentosRealizados);
            relatoriosGerados = valorOuZero(relatoriosGerados);
            rendimento = valorOuZero(rendimento);

            /*
             * Pacientes sem rendimento pago não serão carregados na tabela.
             * Somente valores maiores que zero são incluídos na resposta.
             */
            if (rendimento.compareTo(BigDecimal.ZERO) > 0) {
                linhas.add(
                        new MeuFaturamentoPacienteResponse(
                                paciente.getId(),
                                paciente.getNomeCompleto(),
                                consultasRealizadas,
                                acompanhamentosRealizados,
                                relatoriosGerados,
                                rendimento
                        )
                );

                totalConsultas += consultasRealizadas;
                totalAcompanhamentos += acompanhamentosRealizados;
                totalRelatorios += relatoriosGerados;
                rendimentoTotal = rendimentoTotal.add(rendimento);
            }
        }

        return new MeuFaturamentoResponse(
                linhas,
                totalConsultas,
                totalAcompanhamentos,
                totalRelatorios,
                rendimentoTotal
        );
    }

    private String normalizarNome(String nomePaciente) {
        if (nomePaciente == null) {
            return "";
        }

        return nomePaciente.trim();
    }

    private Long valorOuZero(Long valor) {
        return valor == null ? 0L : valor;
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}