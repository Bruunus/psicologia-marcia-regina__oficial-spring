
package br.com.psicologia.marcia.service.arquivo;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.DTO.arquivo.ArquivoPacienteDocumentosResponse;
import br.com.psicologia.marcia.DTO.arquivo.BuscarPacienteArquivadoRequest;
import br.com.psicologia.marcia.DTO.arquivo.CarregarArquivoPacienteRequest;
import br.com.psicologia.marcia.DTO.arquivo.PacienteArquivadoResponse;
import br.com.psicologia.marcia.DTO.arquivo.QueixaArquivoResponse;
import br.com.psicologia.marcia.DTO.arquivo.ReciboArquivoResponse;
import br.com.psicologia.marcia.DTO.arquivo.RelatorioAtendimentoArquivoResponse;
import br.com.psicologia.marcia.DTO.arquivo.RestaurarTratamentoRequest;
import br.com.psicologia.marcia.DTO.declaracao.DeclaracaoComparecimentoResponse;
import br.com.psicologia.marcia.DTO.laudo.LaudoResponse;
import br.com.psicologia.marcia.DTO.relatorio.psicologico.RelatorioPsicologicoResponse;

import br.com.psicologia.marcia.model.Endereco;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.Psicologo;
import br.com.psicologia.marcia.model.Queixa;

import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.model.enums.StatusPaciente;
import br.com.psicologia.marcia.model.enums.StatusPagamento;

import br.com.psicologia.marcia.repository.financeiro.PagamentoPacienteRepository;
import br.com.psicologia.marcia.repository.paciente.ReadPacienteRepository;
import br.com.psicologia.marcia.repository.paciente.UpdatePacienteRepository;

import br.com.psicologia.marcia.service.acompanhamento.AcompanhamentoPacienteService;
import br.com.psicologia.marcia.service.declaracao.DeclaracaoComparecimentoService;
import br.com.psicologia.marcia.service.laudo.LaudoService;
import br.com.psicologia.marcia.service.psicologo.PsicologoService;
import br.com.psicologia.marcia.service.relatorio.psicologico.RelatorioPsicologicoService;

@Service
public class ArquivoPacienteService {

    private final ReadPacienteRepository readPacienteRepository;
    private final UpdatePacienteRepository updatePacienteRepository;
    private final RelatorioPsicologicoService relatorioPsicologicoService;
    private final DeclaracaoComparecimentoService declaracaoComparecimentoService;
    private final PagamentoPacienteRepository pagamentoPacienteRepository;
    private final LaudoService laudoService;
    private final AcompanhamentoPacienteService acompanhamentoPacienteService;
    private final PsicologoService psicologoService;

    public ArquivoPacienteService(
            ReadPacienteRepository readPacienteRepository,
            UpdatePacienteRepository updatePacienteRepository,
            RelatorioPsicologicoService relatorioPsicologicoService,
            DeclaracaoComparecimentoService declaracaoComparecimentoService,
            PagamentoPacienteRepository pagamentoPacienteRepository,
            LaudoService laudoService,
            AcompanhamentoPacienteService acompanhamentoPacienteService,
            PsicologoService psicologoService
    ) {
        this.readPacienteRepository =
                readPacienteRepository;

        this.updatePacienteRepository =
                updatePacienteRepository;

        this.relatorioPsicologicoService =
                relatorioPsicologicoService;

        this.declaracaoComparecimentoService =
                declaracaoComparecimentoService;

        this.pagamentoPacienteRepository =
                pagamentoPacienteRepository;

        this.laudoService =
                laudoService;

        this.acompanhamentoPacienteService =
                acompanhamentoPacienteService;

        this.psicologoService =
                psicologoService;
    }

    public List<PacienteArquivadoResponse> buscarPacienteArquivado(
            BuscarPacienteArquivadoRequest request
    ) {
        validarPesquisa(request);

        String nomePaciente =
                request.nomePaciente().trim();

        return readPacienteRepository
                .findByNomeCompletoContainingIgnoreCaseAndStatusPacienteOrderByNomeCompletoAsc(
                        nomePaciente,
                        StatusPaciente.ARQUIVADO
                )
                .stream()
                .map(paciente -> new PacienteArquivadoResponse(
                        paciente.getId(),
                        paciente.getNomeCompleto(),
                        paciente.getCpf()
                ))
                .toList();
    }

    public ArquivoPacienteDocumentosResponse carregarDocumentos(
            CarregarArquivoPacienteRequest request
    ) {
        validarCarregamento(request);

        Paciente paciente = readPacienteRepository
                .findByIdAndStatusPaciente(
                        request.pacienteId(),
                        StatusPaciente.ARQUIVADO
                )
                .orElseThrow(() -> new NoSuchElementException(
                        "Paciente arquivado não encontrado."
                ));

        Endereco endereco =
                paciente.getEndereco();

        Psicologo psicologo =
                psicologoService.buscarPsicologoAtivoPrincipal();

        List<QueixaArquivoResponse> queixas =
                montarQueixas(paciente);

        List<RelatorioPsicologicoResponse> relatoriosPsicologicos =
                relatorioPsicologicoService.listarPorPaciente(
                        paciente.getId()
                );

        List<DeclaracaoComparecimentoResponse> declaracoesComparecimento =
                declaracaoComparecimentoService.listarPorPaciente(
                        paciente.getId()
                );

        List<RelatorioAtendimentoArquivoResponse> relatoriosAtendimento =
                acompanhamentoPacienteService
                        .listarParaHistorico(
                                paciente.getId()
                        )
                        .stream()
                        .map(acompanhamento ->
                                new RelatorioAtendimentoArquivoResponse(
                                        acompanhamento.getId(),
                                        acompanhamento.getDataAcompanhamento(),
                                        acompanhamento.getDataAcompanhamentoFormatada(),
                                        acompanhamento.getHoraAcompanhamentoFormatada(),
                                        acompanhamento.getSigiloEtico(),
                                        acompanhamento.getAcompanhamento()
                                )
                        )
                        .toList();

        List<ReciboArquivoResponse> recibos =
                montarRecibos(
                        paciente.getId()
                );

        List<LaudoResponse> laudos =
                laudoService.listarCompletosPorPaciente(
                        paciente.getId()
                );

        return new ArquivoPacienteDocumentosResponse(
                paciente.getId(),
                paciente.getNomeCompleto(),
                paciente.getCpf(),
                paciente.getRg(),
                paciente.getDataNascimento(),
                paciente.getIdade(),
                paciente.getTelefoneContato(),
                endereco != null ? endereco.getLogradouro() : null,
                endereco != null ? endereco.getNumero() : null,
                endereco != null ? endereco.getComplemento() : null,
                endereco != null ? endereco.getBairro() : null,
                endereco != null ? endereco.getCidade() : null,
                endereco != null ? endereco.getUf() : null,
                endereco != null ? endereco.getCep() : null,
                psicologo.getNome(),
                psicologo.getFuncaoEmpresa(),
                psicologo.getCrp(),
                psicologo.getTelefone(),
                psicologo.getEmail(),
                queixas,
                relatoriosPsicologicos,
                declaracoesComparecimento,
                relatoriosAtendimento,
                recibos,
                laudos
        );
    }

    /*
     * RESTAURAR TRATAMENTO
     *
     * Atualiza exclusivamente o status do paciente.
     * Somente pacientes arquivados podem ser restaurados.
     */
    @Transactional
    public void restaurarTratamento(
            RestaurarTratamentoRequest request
    ) {
        validarRestauracao(request);

        Long pacienteId = request.pacienteId();

        int registrosAtualizados =
                updatePacienteRepository.atualizarStatusPaciente(
                        pacienteId,
                        StatusPaciente.ARQUIVADO,
                        StatusPaciente.ATIVADO
                );

        if (registrosAtualizados == 1) {
            return;
        }

        if (!readPacienteRepository.existsById(pacienteId)) {
            throw new NoSuchElementException(
                    "Paciente não encontrado."
            );
        }

        throw new IllegalStateException(
                "O tratamento deste paciente não está arquivado."
        );
    }

    private List<QueixaArquivoResponse> montarQueixas(
            Paciente paciente
    ) {
        Queixa queixa =
                paciente.getQueixa();

        if (queixa == null) {
            return List.of();
        }

        QueixaArquivoResponse documento =
                new QueixaArquivoResponse(
                        queixa.getId(),
                        paciente.getDataCadastro(),
                        queixa.getQueixa()
                );

        return List.of(documento);
    }

    private List<ReciboArquivoResponse> montarRecibos(
            Long pacienteId
    ) {
        return pagamentoPacienteRepository
                .findByPacienteIdAndStatusPagamentoAndStatusDeleteOrderByDtBaixaPagamentoDesc(
                        pacienteId,
                        StatusPagamento.PAGO,
                        StatusDelete.NAO_DELETADO
                )
                .stream()
                .map(pagamento -> new ReciboArquivoResponse(
                        pagamento.getId(),
                        pagamento.getDtBaixaPagamento(),
                        pagamento.getDtPagPrevisto(),
                        pagamento.getValorSessao(),
                        pagamento.getTipoDePagamento(),
                        pagamento.getStatusPagamento()
                ))
                .toList();
    }

    private void validarPesquisa(
            BuscarPacienteArquivadoRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Dados da pesquisa não informados."
            );
        }

        if (
                request.nomePaciente() == null
                || request.nomePaciente().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Nome do paciente não informado."
            );
        }
    }

    private void validarCarregamento(
            CarregarArquivoPacienteRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Dados do carregamento não informados."
            );
        }

        if (request.pacienteId() == null) {
            throw new IllegalArgumentException(
                    "Paciente não informado."
            );
        }

        if (request.pacienteId() <= 0) {
            throw new IllegalArgumentException(
                    "Paciente inválido."
            );
        }
    }

    private void validarRestauracao(
            RestaurarTratamentoRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Dados da restauração não informados."
            );
        }

        if (request.pacienteId() == null) {
            throw new IllegalArgumentException(
                    "Paciente não informado."
            );
        }

        if (request.pacienteId() <= 0) {
            throw new IllegalArgumentException(
                    "Paciente inválido."
            );
        }
    }
}
