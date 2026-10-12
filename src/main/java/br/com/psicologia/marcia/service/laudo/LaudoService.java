package br.com.psicologia.marcia.service.laudo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.DTO.laudo.LaudoListaResponse;
import br.com.psicologia.marcia.DTO.laudo.LaudoResponse;
import br.com.psicologia.marcia.DTO.laudo.LaudoSalvarRequest;
import br.com.psicologia.marcia.model.Laudo;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.repository.laudo.LaudoRepository;
import br.com.psicologia.marcia.repository.paciente.ReadPacienteRepository;


@Service
public class LaudoService {

    private final LaudoRepository laudoRepository;

    private final ReadPacienteRepository readPacienteRepository;


    public LaudoService(
        LaudoRepository laudoRepository,
        ReadPacienteRepository readPacienteRepository
    ) {

        this.laudoRepository =
            laudoRepository;

        this.readPacienteRepository =
            readPacienteRepository;
    }


    /*
     * ============================================================
     * SALVAR
     * ============================================================
     */

    @Transactional
    public LaudoResponse salvar(
        LaudoSalvarRequest request
    ) {

        validarRequest(
            request
        );


        Paciente paciente =
            readPacienteRepository
                .findById(
                    request.pacienteId()
                )
                .orElseThrow(
                    () ->
                        new RuntimeException(
                            "Paciente não encontrado."
                        )
                );


        Laudo laudo =
            new Laudo();


        laudo.setPaciente(
            paciente
        );


        /*
         * ========================================================
         * IDENTIFICAÇÃO
         * ========================================================
         */

        laudo.setDominanciaManual(
            limparTexto(
                request.dominanciaManual()
            )
        );


        laudo.setPeriodoAvaliacaoInicio(
            request.periodoAvaliacaoInicio()
        );


        laudo.setPeriodoAvaliacaoFim(
            request.periodoAvaliacaoFim()
        );


        laudo.setEncaminhamento(
            limparTexto(
                request.encaminhamento()
            )
        );


        /*
         * ========================================================
         * DESCRIÇÃO DA DEMANDA
         * ========================================================
         */

        laudo.setQueixa(
            limparTextoPreservandoQuebras(
                request.queixa()
            )
        );


        /*
         * ========================================================
         * PROCEDIMENTOS
         * ========================================================
         */

        laudo.setQuantidadeSessoes(
            request.quantidadeSessoes()
        );


        laudo.setPeriodo(
            limparTexto(
                request.periodo()
            )
        );


        laudo.setDuracaoMinutos(
            request.duracaoMinutos()
        );


        laudo.setTestesPsicologicos(
            limparTextoPreservandoQuebras(
                request.testesPsicologicos()
            )
        );


        laudo.setTarefasEstudos(
            limparTextoPreservandoQuebras(
                request.tarefasEstudos()
            )
        );


        laudo.setEscalasQuestionarios(
            limparTextoPreservandoQuebras(
                request.escalasQuestionarios()
            )
        );


        /*
         * ========================================================
         * ANÁLISE
         * ========================================================
         */

        laudo.setDadosAnamnese(
            limparTextoPreservandoQuebras(
                request.dadosAnamnese()
            )
        );


        laudo.setDadosAtuais(
            limparTextoPreservandoQuebras(
                request.dadosAtuais()
            )
        );


        laudo.setObservacoesClinicas(
            limparTextoPreservandoQuebras(
                request.observacoesClinicas()
            )
        );


        /*
         * ========================================================
         * CONCLUSÃO
         * ========================================================
         */

        laudo.setConclusao(
            limparTextoPreservandoQuebras(
                request.conclusao()
            )
        );


        /*
         * ========================================================
         * DATA DE EMISSÃO
         * ========================================================
         */

        laudo.setCidadeEmissao(
            limparTexto(
                request.cidadeEmissao()
            )
        );


        laudo.setDiaEmissao(
            request.diaEmissao()
        );


        laudo.setMesEmissao(
            limparTexto(
                request.mesEmissao()
            )
        );


        laudo.setAnoEmissao(
            request.anoEmissao()
        );


        /*
         * ========================================================
         * TABELAS / SEÇÕES
         * ========================================================
         */

        configurarEficienciaIntelectual(
            laudo,
            request
        );


        configurarSecoesOpcionais(
            laudo,
            request
        );


        configurarSecoesObrigatorias(
            laudo
        );


        /*
         * ========================================================
         * EXCLUSÃO LÓGICA
         * ========================================================
         */

        laudo.setStatusDelete(
            false
        );


        /*
         * ========================================================
         * PERSISTÊNCIA
         * ========================================================
         */

        Laudo laudoSalvo =
            laudoRepository.save(
                laudo
            );


        return converterParaResponse(
            laudoSalvo
        );
    }


    /*
     * ============================================================
     * EXCLUIR
     * ============================================================
     */

    @Transactional
    public void excluir(
        Long laudoId
    ) {

        if (
            laudoId == null
        ) {

            throw new RuntimeException(
                "O ID do laudo é obrigatório."
            );
        }


        Laudo laudo =
            laudoRepository
                .findByIdAndStatusDeleteFalse(
                    laudoId
                )
                .orElseThrow(
                    () ->
                        new RuntimeException(
                            "Laudo psicológico não encontrado."
                        )
                );


        laudo.setStatusDelete(
            true
        );


        laudoRepository.save(
            laudo
        );
    }


    /*
     * ============================================================
     * LISTAR POR PACIENTE
     * ============================================================
     */

    @Transactional(readOnly = true)
    public List<LaudoListaResponse> listarPorPaciente(
        Long pacienteId
    ) {

        if (
            pacienteId == null
        ) {

            throw new RuntimeException(
                "O ID do paciente é obrigatório."
            );
        }


        return laudoRepository
            .listarResumoPorPaciente(
                pacienteId
            );
    }


    /*
     * ============================================================
     * LISTAR COMPLETOS PARA O ARQUIVO DO PACIENTE
     * ============================================================
     */

    @Transactional(readOnly = true)
    public List<LaudoResponse> listarCompletosPorPaciente(
        Long pacienteId
    ) {

        if (
            pacienteId == null
        ) {

            throw new RuntimeException(
                "O ID do paciente é obrigatório."
            );
        }


        return laudoRepository
            .findByPacienteIdAndStatusDeleteFalseOrderByDataCriacaoDesc(
                pacienteId
            )
            .stream()
            .map(
                this::converterParaResponse
            )
            .toList();
    }


    /*
     * ============================================================
     * EFICIÊNCIA INTELECTUAL
     * ============================================================
     */

    private void configurarEficienciaIntelectual(
        Laudo laudo,
        LaudoSalvarRequest request
    ) {

        boolean incluirEficienciaIntelectual =
            valorBooleano(
                request.incluirEficienciaIntelectual()
            );


        laudo.setIncluirEficienciaIntelectual(
            incluirEficienciaIntelectual
        );


        /*
         * O campo textual pode permanecer nulo.
         *
         * A tabela/seção é controlada pelo boolean.
         */

        laudo.setEficienciaIntelectual(
            null
        );
    }


    /*
     * ============================================================
     * SEÇÕES OPCIONAIS
     * ============================================================
     */

    private void configurarSecoesOpcionais(
        Laudo laudo,
        LaudoSalvarRequest request
    ) {

        laudo.setIncluirEscalaWasi(
            valorBooleano(
                request.incluirEscalaWasi()
            )
        );


        laudo.setIncluirEscalaSrs2(
            valorBooleano(
                request.incluirEscalaSrs2()
            )
        );
    }


    /*
     * ============================================================
     * SEÇÕES OBRIGATÓRIAS
     * ============================================================
     */

    private void configurarSecoesObrigatorias(
        Laudo laudo
    ) {

        laudo.setIncluirFuncoesExecutivasAtencionais(
            true
        );


        laudo.setIncluirMemoriaVisualAuditivoVerbal(
            true
        );


        laudo.setIncluirMemoriaAprendizagem(
            true
        );


        laudo.setIncluirFuncoesVisoespaciais(
            true
        );
    }


    /*
     * ============================================================
     * VALIDAÇÕES
     * ============================================================
     */

    private void validarRequest(
        LaudoSalvarRequest request
    ) {

        if (
            request == null
        ) {

            throw new RuntimeException(
                "Os dados do laudo são obrigatórios."
            );
        }


        if (
            request.pacienteId() == null
        ) {

            throw new RuntimeException(
                "O paciente é obrigatório."
            );
        }


        if (
            request.periodoAvaliacaoInicio() != null
            &&
            request.periodoAvaliacaoFim() != null
            &&
            request
                .periodoAvaliacaoFim()
                .isBefore(
                    request.periodoAvaliacaoInicio()
                )
        ) {

            throw new RuntimeException(
                "A data final do período de avaliação não pode ser anterior à data inicial."
            );
        }


        if (
            request.quantidadeSessoes() != null
            &&
            request.quantidadeSessoes() <= 0
        ) {

            throw new RuntimeException(
                "A quantidade de sessões deve ser maior que zero."
            );
        }


        if (
            request.duracaoMinutos() != null
            &&
            request.duracaoMinutos() <= 0
        ) {

            throw new RuntimeException(
                "A duração deve ser maior que zero."
            );
        }
    }


    /*
     * ============================================================
     * RESPONSE
     * ============================================================
     */

    private LaudoResponse converterParaResponse(
        Laudo laudo
    ) {

        Paciente paciente =
            laudo.getPaciente();


        return new LaudoResponse(

            laudo.getId(),

            paciente != null
                ? paciente.getId()
                : null,

            paciente != null
                ? paciente.getNomeCompleto()
                : null,

            laudo.getDominanciaManual(),

            laudo.getPeriodoAvaliacaoInicio(),

            laudo.getPeriodoAvaliacaoFim(),

            laudo.getEncaminhamento(),

            laudo.getQueixa(),

            laudo.getQuantidadeSessoes(),

            laudo.getPeriodo(),

            laudo.getDuracaoMinutos(),

            laudo.getTestesPsicologicos(),

            laudo.getTarefasEstudos(),

            laudo.getEscalasQuestionarios(),

            laudo.getDadosAnamnese(),

            laudo.getDadosAtuais(),

            laudo.getObservacoesClinicas(),

            laudo.getEficienciaIntelectual(),

            laudo.getConclusao(),

            laudo.getIncluirEficienciaIntelectual(),

            laudo.getIncluirEscalaWasi(),

            laudo.getIncluirFuncoesExecutivasAtencionais(),

            laudo.getIncluirMemoriaVisualAuditivoVerbal(),

            laudo.getIncluirMemoriaAprendizagem(),

            laudo.getIncluirFuncoesVisoespaciais(),

            laudo.getIncluirEscalaSrs2(),

            laudo.getDataCriacao(),

            laudo.getDataAtualizacao()
        );
    }


    /*
     * ============================================================
     * UTILITÁRIOS
     * ============================================================
     */

    private boolean valorBooleano(
        Boolean valor
    ) {

        return Boolean.TRUE.equals(
            valor
        );
    }


    private String limparTexto(
        String texto
    ) {

        if (
            texto == null
        ) {

            return null;
        }


        String textoLimpo =
            texto
                .trim()
                .replaceAll(
                    "\\s+",
                    " "
                );


        if (
            textoLimpo.isBlank()
        ) {

            return null;
        }


        return textoLimpo;
    }


    private String limparTextoPreservandoQuebras(
        String texto
    ) {

        if (
            texto == null
        ) {

            return null;
        }


        String textoLimpo =
            texto
                .replace(
                    "\r\n",
                    "\n"
                )
                .replace(
                    '\r',
                    '\n'
                )
                .trim();


        if (
            textoLimpo.isBlank()
        ) {

            return null;
        }


        return textoLimpo;
    }
}