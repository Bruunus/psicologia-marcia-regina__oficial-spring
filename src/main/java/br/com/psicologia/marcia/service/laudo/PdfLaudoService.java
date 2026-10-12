package br.com.psicologia.marcia.service.laudo;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import br.com.psicologia.marcia.DTO.laudo.LaudoVisualizarRequest;
import br.com.psicologia.marcia.DTO.laudo.PdfLaudoGeradoDTO;
import br.com.psicologia.marcia.model.Laudo;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.Psicologo;
import br.com.psicologia.marcia.repository.laudo.LaudoRepository;
import br.com.psicologia.marcia.service.documento.DocumentoLayoutConfig;
import br.com.psicologia.marcia.service.documento.DocumentoPdfCabecalhoEvent;
import br.com.psicologia.marcia.service.psicologo.PsicologoService;


@Service
public class PdfLaudoService {

    private static final int TAMANHO_FONTE_PADRAO =
        11;

    private static final int TAMANHO_FONTE_SUBTITULO =
        13;

    private static final int TAMANHO_FONTE_TABELA =
        9;

    private static final int TAMANHO_FONTE_LEGENDA =
        8;

    private static final String COR_SUBTITULO =
        "4472C4";

    private static final String COR_CABECALHO_TABELA =
        "8EAADB";


    private final LaudoRepository laudoRepository;

    private final PsicologoService psicologoService;

    private final DocumentoLayoutConfig documentoLayoutConfig;


    public PdfLaudoService(
        LaudoRepository laudoRepository,
        PsicologoService psicologoService,
        DocumentoLayoutConfig documentoLayoutConfig
    ) {

        this.laudoRepository =
            laudoRepository;

        this.psicologoService =
            psicologoService;

        this.documentoLayoutConfig =
            documentoLayoutConfig;
    }


    @Transactional(readOnly = true)
    public PdfLaudoGeradoDTO gerarPdf(
        LaudoVisualizarRequest request
    ) {

        validarRequest(
            request
        );


        Laudo laudo =
            laudoRepository
                .findByIdAndStatusDeleteFalse(
                    request.laudoId()
                )
                .orElseThrow(
                    () ->
                        new RuntimeException(
                            "Laudo psicológico não encontrado."
                        )
                );


        Paciente paciente =
            laudo.getPaciente();


        if (
            paciente == null
        ) {

            throw new RuntimeException(
                "Paciente não encontrado para o laudo informado."
            );
        }


        Psicologo psicologo =
            psicologoService
                .buscarPsicologoAtivoPrincipal();


        try (
            ByteArrayOutputStream saida =
                new ByteArrayOutputStream()
        ) {

            Document documento =
                new Document(
                    PageSize.A4,
                    documentoLayoutConfig.margemEsquerdaPdf(),
                    documentoLayoutConfig.margemDireitaPdf(),
                    documentoLayoutConfig.margemSuperiorPdf(),
                    documentoLayoutConfig.margemInferiorPdf()
                );


            PdfWriter writer =
                PdfWriter.getInstance(
                    documento,
                    saida
                );


            writer.setPageEvent(
                new DocumentoPdfCabecalhoEvent(
                    documentoLayoutConfig,
                    psicologo
                )
            );


            documento.open();


            criarTituloPrincipal(
                documento
            );


            criarSubtitulo(
                documento,
                "Identificação"
            );


            criarIdentificacao(
                documento,
                paciente,
                laudo
            );


            criarSubtitulo(
                documento,
                "Descrição da demanda"
            );


            criarParagrafoCorpo(
                documento,
                laudo.getQueixa()
            );


            criarSubtitulo(
                documento,
                "Procedimentos"
            );


            criarFilhoSubtitulo(
                documento,
                "Informações Preliminares"
            );


            criarInformacoesPreliminares(
                documento,
                paciente,
                laudo
            );


            criarParagrafoProcedimentos(
                documento,
                laudo
            );


            criarSubtitulo(
                documento,
                "Testes Psicológicos"
            );


            criarFilhoSubtitulo(
                documento,
                "Tarefas com estudos preliminares de padronização para a população brasileira e tarefas qualitativas com validação científica"
            );


            criarParagrafoCorpo(
                documento,
                laudo.getTarefasEstudos()
            );


            criarFilhoSubtitulo(
                documento,
                "Escalas e questionários"
            );


            criarParagrafoCorpo(
                documento,
                laudo.getEscalasQuestionarios()
            );


            criarSubtitulo(
                documento,
                "Análise"
            );


            criarFilhoSubtitulo(
                documento,
                "Dados da Anamnese"
            );


            criarParagrafoCorpo(
                documento,
                laudo.getDadosAnamnese()
            );


            criarFilhoSubtitulo(
                documento,
                "Dados Atuais"
            );


            criarParagrafoCorpo(
                documento,
                laudo.getDadosAtuais()
            );


            criarFilhoSubtitulo(
                documento,
                "Observações clínicas"
            );


            criarParagrafoCorpo(
                documento,
                laudo.getObservacoesClinicas()
            );


            if (
                Boolean.TRUE.equals(
                    laudo.getIncluirEficienciaIntelectual()
                )
            ) {

                criarSecaoEficienciaIntelectual(
                    documento,
                    laudo
                );
            }


            if (
                Boolean.TRUE.equals(
                    laudo.getIncluirEscalaWasi()
                )
            ) {

                criarSecaoWasi(
                    documento
                );
            }


            criarSecaoFuncoesExecutivas(
                documento
            );


            criarSecaoMemoriaVisualAuditivoVerbal(
                documento
            );


            criarSecaoMemoriaAprendizagem(
                documento
            );


            criarSecaoFuncoesVisoespaciais(
                documento
            );


            if (
                Boolean.TRUE.equals(
                    laudo.getIncluirEscalaSrs2()
                )
            ) {

                criarSecaoSrs2(
                    documento
                );
            }


            criarFilhoSubtitulo(
                documento,
                "Conclusão"
            );


            criarParagrafoCorpo(
                documento,
                laudo.getConclusao()
            );


            criarDataEmissao(
                documento,
                laudo
            );


            criarAssinatura(
                documento,
                writer,
                psicologo
            );


            documento.close();


            return new PdfLaudoGeradoDTO(
                saida.toByteArray(),
                montarNomeArquivo(
                    paciente,
                    laudo
                )
            );

        } catch (
            DocumentException e
        ) {

            throw new RuntimeException(
                "Erro ao gerar PDF do laudo psicológico.",
                e
            );

        } catch (
            Exception e
        ) {

            throw new RuntimeException(
                "Erro inesperado ao gerar PDF do laudo psicológico.",
                e
            );
        }
    }


    private void criarTituloPrincipal(
        Document documento
    ) throws DocumentException {

        Paragraph titulo =
            new Paragraph(
                "Laudo Psicológico",
                fonteTitulo()
            );


        titulo.setAlignment(
            Element.ALIGN_CENTER
        );

        titulo.setSpacingBefore(
            8f
        );

        titulo.setSpacingAfter(
            0f
        );


        documento.add(
            titulo
        );


        Paragraph subtitulo =
            new Paragraph(
                "Com enfoque neuropsicológico",
                fonteTextoNegrito()
            );


        subtitulo.setAlignment(
            Element.ALIGN_CENTER
        );

        subtitulo.setSpacingBefore(
            0f
        );

        subtitulo.setSpacingAfter(
            45f
        );


        documento.add(
            subtitulo
        );
    }


    private void criarIdentificacao(
        Document documento,
        Paciente paciente,
        Laudo laudo
    ) throws DocumentException {

        criarLinhaIdentificacao(
            documento,
            "Nome: ",
            paciente.getNomeCompleto()
        );

        criarLinhaIdentificacao(
            documento,
            "Data de nascimento: ",
            formatarData(
                paciente.getDataNascimento()
            )
        );

        criarLinhaIdentificacao(
            documento,
            "Idade na Avaliação: ",
            formatarIdadeNaAvaliacao(
                paciente,
                laudo
            )
        );

        criarLinhaIdentificacao(
            documento,
            "Escolaridade: ",
            paciente.getGrauEscolaridade()
        );

        criarLinhaIdentificacao(
            documento,
            "Profissão: ",
            paciente.getProfissao()
        );

        criarLinhaIdentificacao(
            documento,
            "Dominância manual: ",
            laudo.getDominanciaManual()
        );

        criarLinhaIdentificacao(
            documento,
            "Período da Avaliação: ",
            montarPeriodoAvaliacao(
                laudo
            )
        );

        criarLinhaIdentificacao(
            documento,
            "Encaminhamento: ",
            laudo.getEncaminhamento()
        );
    }


    private void criarLinhaIdentificacao(
        Document documento,
        String rotulo,
        String conteudo
    ) throws DocumentException {

        Paragraph paragrafo =
            new Paragraph();


        paragrafo.setAlignment(
            Element.ALIGN_LEFT
        );

        paragrafo.setSpacingAfter(
            3f
        );


        paragrafo.add(
            new Chunk(
                rotulo,
                fonteTextoNegrito()
            )
        );


        paragrafo.add(
            new Chunk(
                valor(
                    conteudo
                ),
                fonteTexto()
            )
        );


        documento.add(
            paragrafo
        );
    }


    private void criarInformacoesPreliminares(
        Document documento,
        Paciente paciente,
        Laudo laudo
    ) throws DocumentException {

        StringBuilder texto =
            new StringBuilder();


        texto.append(
            "Este relatório foi elaborado a partir das circunstâncias do momento atual para fins clínicos e os resultados aqui descritos são influenciados por variáveis fisiológicas,ambientais e culturais. Todas as informações de anamnese foram obtidas por "
        );


        texto.append(
            valor(
                paciente.getNomeCompleto()
            )
        );


        if (
            pacienteMenorDeIdade(
                paciente,
                laudo
            )
        ) {

            String responsavel =
                valor(
                    paciente.getResponsavel()
                );


            if (
                !responsavel.isBlank()
            ) {

                texto.append(
                    " e responsável "
                );

                texto.append(
                    responsavel
                );
            }
        }


        texto.append(
            "."
        );


        criarParagrafoCorpo(
            documento,
            texto.toString()
        );
    }


    private void criarParagrafoProcedimentos(
        Document documento,
        Laudo laudo
    ) throws DocumentException {

        String quantidadeSessoes =
            laudo.getQuantidadeSessoes() == null
                ? ""
                : String.valueOf(
                    laudo.getQuantidadeSessoes()
                );


        String texto =
            "Foram realizadas "
                +
            quantidadeSessoes
                +
            " sessões no período de "
                +
            valor(
                laudo.getPeriodo()
            )
                +
            ",com "
                +
            formatarDuracao(
                laudo.getDuracaoMinutos()
            )
                +
            " de duração.";


        criarParagrafoCorpo(
            documento,
            texto
        );
    }


    private void criarSecaoEficienciaIntelectual(
        Document documento,
        Laudo laudo
    ) throws DocumentException {

        criarFilhoSubtitulo(
            documento,
            "Eficiência intelectual"
        );


        criarParagrafoCorpo(
            documento,
            laudo.getEficienciaIntelectual()
        );


        criarParagrafoCorpo(
            documento,
            "A integração de várias funções para resolução de problemas,mediadas pelo pensamento,linguagem,conceituação e abstração fazem parte da inteligência global. As escalas Wechsler podem ser entendidas como instrumentos que avaliam a inteligência global,bem como dois tipos de inteligência:a cristalizada,relacionada aos subtestes Vocabulário e Compreensão,e a fluida,relacionada aos subtestes Cubos e Raciocínio Matricial. Importante ressaltar que o conceito de QI possui importância diagnóstica,uma vez que se acentuam os diversos componentes da cognição."
        );
    }


    private void criarSecaoWasi(
        Document documento
    ) throws DocumentException {

        criarFilhoSubtitulo(
            documento,
            "Escala de Inteligência WASI"
        );


        PdfPTable tabela =
            criarTabela(
                new float[]{
                    1.9f,
                    2.3f,
                    3.2f,
                    1.9f
                }
            );


        adicionarCabecalho(
            tabela,
            "Índices",
            "Pontos compostos",
            "Intervalo de confiança 95%",
            "Classificação"
        );


        adicionarLinhasVazias(
            tabela,
            4,
            4
        );


        documento.add(
            tabela
        );


        criarLegenda(
            documento,
            "Valores de QI:Muito abaixo da média(≤79);Médio inferior(80-89);Médio(90-109);Médio superior(110-119);Superior(120-129);Muito superior(≥130)."
        );
    }


    private void criarSecaoFuncoesExecutivas(
        Document documento
    ) throws DocumentException {

        criarFilhoSubtitulo(
            documento,
            "Funções Executivas e Atencionais"
        );


        criarParagrafoCorpo(
            documento,
            "Funções executivas envolvem habilidades cognitivas e princípios de organizações necessárias para lidar com planejamento e execução de atividades,incluindo iniciação em tarefas,memória operacional,alternância,flexibilidade e controle inibitório. A atenção é o processo mediante o qual se diferenciam aqueles estímulos que serão utilizados para alcançar ou atingir determinados objetivos ou metas,enquanto são ignorados aqueles que podem ocasionar algum tipo de interferência. Os estímulos relevantes são os considerados alvo e os que produzem interferência são os chamados distratores."
        );


        PdfPTable tabela =
            criarTabela(
                new float[]{
                    1f,
                    1f
                }
            );


        adicionarCabecalho(
            tabela,
            "Função",
            "Classificação"
        );


        adicionarLinhasVazias(
            tabela,
            4,
            2
        );


        documento.add(
            tabela
        );


        criarLegenda(
            documento,
            "WECHSLER,David et al. Escala Wechsler abreviada de inteligência – WASI. 1ª Edição. São Paulo:Casa do Psicólogo,2014.\n"
                +
            "REY,André et al. Figuras complexas de Rey:teste de cópia e de reprodução de memória de figuras geométricas complexas. São Paulo:Casa do Psicólogo,2014.\n"
                +
            "SEDÓ,M.;PAULA,J. Malloy-Diniz. Teste dos Cinco Dígitos. Versão Brasileira. Tradução Oto Mendonça,2015."
        );
    }


    private void criarSecaoMemoriaVisualAuditivoVerbal(
        Document documento
    ) throws DocumentException {

        criarFilhoSubtitulo(
            documento,
            "Memória visual e/ou auditivo-verbal"
        );


        criarParagrafoCorpo(
            documento,
            "Memória é a habilidade de adquirir,reter e evocar informações. É composta por subsistemas e subdividida em diferentes tipos."
        );


        PdfPTable tabela =
            criarTabela(
                new float[]{
                    1f,
                    1f
                }
            );


        adicionarCabecalho(
            tabela,
            "Função",
            "Classificação"
        );


        adicionarLinhasVazias(
            tabela,
            4,
            2
        );


        documento.add(
            tabela
        );
    }


    private void criarSecaoMemoriaAprendizagem(
        Document documento
    ) throws DocumentException {

        criarFilhoSubtitulo(
            documento,
            "Memória e aprendizagem"
        );


        PdfPTable tabela =
            criarTabela(
                new float[]{
                    2.8f,
                    1.625f,
                    1.625f,
                    1.625f,
                    1.625f
                }
            );


        adicionarCabecalho(
            tabela,
            "Categorias",
            "Pontuação",
            "Percentil",
            "Classificação",
            ""
        );


        String[] linhas = {
            "Primeiras etapas da aprendizagem",
            "",
            "",
            "",
            "",
            "Distrator",
            "Evocação Imediata",
            "Evocação Tardia",
            "Reconhecimento",
            "Índice de aprendizagem"
        };


        for (
            String rotulo :
            linhas
        ) {

            adicionarCelula(
                tabela,
                rotulo,
                false
            );


            for (
                int coluna = 1;
                coluna < 5;
                coluna++
            ) {

                adicionarCelula(
                    tabela,
                    "",
                    false
                );
            }
        }


        documento.add(
            tabela
        );
    }


    private void criarSecaoFuncoesVisoespaciais(
        Document documento
    ) throws DocumentException {

        criarFilhoSubtitulo(
            documento,
            "Funções viso-espaciais/praxias viso-construtivas"
        );


        criarParagrafoCorpo(
            documento,
            "Habilidades que são mediadas por funções visuais e que permitem executar ações voltadas a um fim num plano concreto por meio da atividade motora."
        );


        PdfPTable tabela =
            criarTabela(
                new float[]{
                    1f,
                    1f
                }
            );


        adicionarCabecalho(
            tabela,
            "Função",
            "Classificação"
        );


        adicionarLinhasVazias(
            tabela,
            4,
            2
        );


        documento.add(
            tabela
        );
    }


    private void criarSecaoSrs2(
        Document documento
    ) throws DocumentException {

        criarFilhoSubtitulo(
            documento,
            "Escala de Responsividade Social – SRS-2"
        );


        criarParagrafoCorpo(
            documento,
            "A Escala de Responsividade Social,Segunda Edição(SRS-2)tem como objetivo mensurar sintomas associados ao Transtorno do Espectro Autista(TEA),bem como classificá-los em níveis leves,moderados ou severos. Sua avaliação se faz de forma global e específica,já que agrupa os sintomas em subcategorias,incluindo Escalas Compatíveis ao DSM-5 e Subescalas de Intervenção. Por esse motivo,pode ser utilizada para iniciar processos diagnósticos,como rastreio,e para o planejamento de intervenções clínicas e ocupacionais."
        );


        PdfPTable tabela =
            criarTabela(
                new float[]{
                    1f,
                    1f,
                    1f
                }
            );


        adicionarCabecalho(
            tabela,
            "Função",
            "Autorrelato",
            "Heterorrelato"
        );


        adicionarLinhasVazias(
            tabela,
            4,
            3
        );


        documento.add(
            tabela
        );
    }


    private PdfPTable criarTabela(
        float[] larguras
    ) throws DocumentException {

        PdfPTable tabela =
            new PdfPTable(
                larguras.length
            );


        tabela.setWidthPercentage(
            100f
        );

        tabela.setWidths(
            larguras
        );

        tabela.setSpacingBefore(
            8f
        );

        tabela.setSpacingAfter(
            8f
        );


        return tabela;
    }


    private void adicionarCabecalho(
        PdfPTable tabela,
        String... textos
    ) {

        for (
            String texto :
            textos
        ) {

            PdfPCell celula =
                new PdfPCell(
                    new Phrase(
                        valor(
                            texto
                        ),
                        fonteTabelaNegrito()
                    )
                );


            celula.setHorizontalAlignment(
                Element.ALIGN_CENTER
            );

            celula.setVerticalAlignment(
                Element.ALIGN_MIDDLE
            );

            celula.setPadding(
                5f
            );

            celula.setBackgroundColor(
                new java.awt.Color(
                    142,
                    170,
                    219
                )
            );


            tabela.addCell(
                celula
            );
        }
    }


    private void adicionarLinhasVazias(
        PdfPTable tabela,
        int quantidadeLinhas,
        int quantidadeColunas
    ) {

        for (
            int linha = 0;
            linha < quantidadeLinhas;
            linha++
        ) {

            for (
                int coluna = 0;
                coluna < quantidadeColunas;
                coluna++
            ) {

                adicionarCelula(
                    tabela,
                    "",
                    false
                );
            }
        }
    }


    private void adicionarCelula(
        PdfPTable tabela,
        String texto,
        boolean negrito
    ) {

        Font fonte =
            negrito
                ? fonteTabelaNegrito()
                : fonteTabela();


        PdfPCell celula =
            new PdfPCell(
                new Phrase(
                    valor(
                        texto
                    ),
                    fonte
                )
            );


        celula.setVerticalAlignment(
            Element.ALIGN_MIDDLE
        );

        celula.setPadding(
            5f
        );

        celula.setMinimumHeight(
            24f
        );


        tabela.addCell(
            celula
        );
    }


    private void criarSubtitulo(
        Document documento,
        String texto
    ) throws DocumentException {

        Paragraph paragrafo =
            new Paragraph(
                valor(
                    texto
                ),
                fonteSubtitulo()
            );


        paragrafo.setAlignment(
            Element.ALIGN_LEFT
        );

        paragrafo.setSpacingBefore(
            10f
        );

        paragrafo.setSpacingAfter(
            14f
        );


        documento.add(
            paragrafo
        );
    }


    private void criarFilhoSubtitulo(
        Document documento,
        String texto
    ) throws DocumentException {

        Paragraph paragrafo =
            new Paragraph(
                valor(
                    texto
                ),
                fonteTextoNegrito()
            );


        paragrafo.setAlignment(
            Element.ALIGN_LEFT
        );

        paragrafo.setSpacingBefore(
            18f
        );

        paragrafo.setSpacingAfter(
            8f
        );


        documento.add(
            paragrafo
        );
    }


    private void criarParagrafoCorpo(
        Document documento,
        String texto
    ) throws DocumentException {

        if (
            texto == null
            ||
            texto.isBlank()
        ) {

            return;
        }


        Paragraph paragrafo =
            new Paragraph(
                valor(
                    texto
                ),
                fonteTexto()
            );


        paragrafo.setAlignment(
            Element.ALIGN_JUSTIFIED
        );


        paragrafo.setFirstLineIndent(
            documentoLayoutConfig
                .recuoPrimeiraLinhaCorpoPdf()
        );


        paragrafo.setLeading(
            0,
            1.15f
        );

        paragrafo.setSpacingBefore(
            4f
        );

        paragrafo.setSpacingAfter(
            8f
        );


        documento.add(
            paragrafo
        );
    }


    private void criarLegenda(
        Document documento,
        String texto
    ) throws DocumentException {

        Paragraph paragrafo =
            new Paragraph(
                valor(
                    texto
                ),
                fonteLegenda()
            );


        paragrafo.setAlignment(
            Element.ALIGN_LEFT
        );

        paragrafo.setSpacingBefore(
            2f
        );

        paragrafo.setSpacingAfter(
            6f
        );


        documento.add(
            paragrafo
        );
    }


    /*
     * ============================================================
     * DATA DE EMISSÃO
     * ============================================================
     */

    private void criarDataEmissao(
        Document documento,
        Laudo laudo
    ) throws DocumentException {

        String texto =
            montarTextoDataEmissao(
                laudo
            );


        if (
            texto.isBlank()
        ) {

            return;
        }


        Paragraph paragrafo =
            new Paragraph(
                texto,
                fonteTexto()
            );


        paragrafo.setAlignment(
            Element.ALIGN_LEFT
        );

        paragrafo.setSpacingBefore(
            14f
        );

        paragrafo.setSpacingAfter(
            20f
        );


        documento.add(
            paragrafo
        );
    }


    private String montarTextoDataEmissao(
        Laudo laudo
    ) {

        String cidade =
            valor(
                laudo.getCidadeEmissao()
            ).trim();


        Integer dia =
            laudo.getDiaEmissao();


        String mes =
            valor(
                laudo.getMesEmissao()
            ).trim();


        Integer ano =
            laudo.getAnoEmissao();


        if (
            cidade.isBlank()
            &&
            dia == null
            &&
            mes.isBlank()
            &&
            ano == null
        ) {

            return "";
        }


        StringBuilder texto =
            new StringBuilder();


        if (
            !cidade.isBlank()
        ) {

            texto.append(
                cidade
            );
        }


        if (
            dia != null
        ) {

            if (
                texto.length() > 0
            ) {

                texto.append(
                    ", "
                );
            }


            texto.append(
                dia
            );
        }


        if (
            !mes.isBlank()
        ) {

            if (
                dia != null
            ) {

                texto.append(
                    " de "
                );

            } else if (
                texto.length() > 0
            ) {

                texto.append(
                    ", "
                );
            }


            texto.append(
                mes
            );
        }


        if (
            ano != null
        ) {

            if (
                !mes.isBlank()
            ) {

                texto.append(
                    " de "
                );

            } else if (
                texto.length() > 0
            ) {

                texto.append(
                    ", "
                );
            }


            texto.append(
                ano
            );
        }


        texto.append(
            "."
        );


        return texto.toString();
    }


    private void criarAssinatura(
        Document documento,
        PdfWriter writer,
        Psicologo psicologo
    ) throws DocumentException {

        float posicaoAtual =
            writer.getVerticalPosition(
                false
            );


        if (
            posicaoAtual
                <=
            documentoLayoutConfig
                .posicaoMinimaAssinaturaPdf()
        ) {

            documento.newPage();
        }


        PdfPTable tabela =
            new PdfPTable(
                1
            );


        tabela.setTotalWidth(
            documento.right()
                -
            documento.left()
        );


        tabela.setLockedWidth(
            true
        );


        tabela.addCell(
            criarCelulaAssinatura(
                "_____________________________"
            )
        );


        tabela.addCell(
            criarCelulaAssinatura(
                valor(
                    psicologo.getNome()
                )
            )
        );


        tabela.addCell(
            criarCelulaAssinatura(
                "Psicóloga CRP: "
                    +
                valor(
                    psicologo.getCrp()
                )
            )
        );


        tabela.writeSelectedRows(
            0,
            -1,
            documento.left(),
            documentoLayoutConfig
                .posicaoTopoAssinaturaPdf(),
            writer.getDirectContent()
        );
    }


    private PdfPCell criarCelulaAssinatura(
        String texto
    ) {

        PdfPCell celula =
            new PdfPCell(
                new Phrase(
                    valor(
                        texto
                    ),
                    fonteTexto()
                )
            );


        celula.setBorder(
            Rectangle.NO_BORDER
        );

        celula.setHorizontalAlignment(
            Element.ALIGN_CENTER
        );

        celula.setPaddingTop(
            0f
        );

        celula.setPaddingBottom(
            4f
        );


        return celula;
    }


    private Font fonteTitulo() {

        return FontFactory.getFont(
            documentoLayoutConfig
                .fonteTextoPdf(),
            14,
            Font.BOLD
        );
    }


    private Font fonteSubtitulo() {

        Font fonte =
            FontFactory.getFont(
                "Arial",
                TAMANHO_FONTE_SUBTITULO,
                Font.BOLD
            );


        fonte.setColor(
            68,
            114,
            196
        );


        return fonte;
    }


    private Font fonteTexto() {

        return FontFactory.getFont(
            documentoLayoutConfig
                .fonteTextoPdf(),
            TAMANHO_FONTE_PADRAO,
            Font.NORMAL
        );
    }


    private Font fonteTextoNegrito() {

        return FontFactory.getFont(
            documentoLayoutConfig
                .fonteTextoPdf(),
            TAMANHO_FONTE_PADRAO,
            Font.BOLD
        );
    }


    private Font fonteTabela() {

        return FontFactory.getFont(
            documentoLayoutConfig
                .fonteTextoPdf(),
            TAMANHO_FONTE_TABELA,
            Font.NORMAL
        );
    }


    private Font fonteTabelaNegrito() {

        return FontFactory.getFont(
            documentoLayoutConfig
                .fonteTextoPdf(),
            TAMANHO_FONTE_TABELA,
            Font.BOLD
        );
    }


    private Font fonteLegenda() {

        return FontFactory.getFont(
            documentoLayoutConfig
                .fonteTextoPdf(),
            TAMANHO_FONTE_LEGENDA,
            Font.NORMAL
        );
    }


    private boolean pacienteMenorDeIdade(
        Paciente paciente,
        Laudo laudo
    ) {

        if (
            paciente.getDataNascimento() == null
        ) {

            return false;
        }


        LocalDate dataReferencia =
            laudo.getPeriodoAvaliacaoInicio() != null
                ? laudo.getPeriodoAvaliacaoInicio()
                : LocalDate.now();


        return Period
            .between(
                paciente.getDataNascimento(),
                dataReferencia
            )
            .getYears()
            <
            18;
    }


    private String formatarIdadeNaAvaliacao(
        Paciente paciente,
        Laudo laudo
    ) {

        if (
            paciente.getDataNascimento() == null
        ) {

            return "";
        }


        LocalDate dataReferencia =
            laudo.getPeriodoAvaliacaoInicio() != null
                ? laudo.getPeriodoAvaliacaoInicio()
                : LocalDate.now();


        int idade =
            Period
                .between(
                    paciente.getDataNascimento(),
                    dataReferencia
                )
                .getYears();


        return idade
            +
            " anos";
    }


    private String montarPeriodoAvaliacao(
        Laudo laudo
    ) {

        String inicio =
            formatarData(
                laudo.getPeriodoAvaliacaoInicio()
            );


        String fim =
            formatarData(
                laudo.getPeriodoAvaliacaoFim()
            );


        if (
            inicio.isBlank()
            &&
            fim.isBlank()
        ) {

            return "";
        }


        if (
            inicio.isBlank()
        ) {

            return fim;
        }


        if (
            fim.isBlank()
        ) {

            return inicio;
        }


        return inicio
            +
            " à "
            +
            fim;
    }


    private String formatarData(
        LocalDate data
    ) {

        if (
            data == null
        ) {

            return "";
        }


        return data.format(
            DateTimeFormatter.ofPattern(
                "dd/MM/yyyy"
            )
        );
    }


    private String formatarDuracao(
        Integer duracaoMinutos
    ) {

        if (
            duracaoMinutos == null
            ||
            duracaoMinutos <= 0
        ) {

            return "1 hora";
        }


        int horas =
            duracaoMinutos
                /
            60;


        int minutos =
            duracaoMinutos
                %
            60;


        if (
            horas == 0
        ) {

            return minutos == 1
                ? "1 minuto"
                : minutos + " minutos";
        }


        String textoHoras =
            horas == 1
                ? "1 hora"
                : horas + " horas";


        if (
            minutos == 0
        ) {

            return textoHoras;
        }


        String textoMinutos =
            minutos == 1
                ? "1 minuto"
                : minutos + " minutos";


        return textoHoras
            +
            " e "
            +
            textoMinutos;
    }


    private String montarNomeArquivo(
        Paciente paciente,
        Laudo laudo
    ) {

        String nomePaciente =
            paciente.getNomeCompleto() != null
                &&
            !paciente
                .getNomeCompleto()
                .isBlank()
                    ? paciente.getNomeCompleto()
                    : "paciente";


        String data =
            laudo.getDataCriacao() != null
                ? laudo
                    .getDataCriacao()
                    .format(
                        DateTimeFormatter.ofPattern(
                            "dd-MM-yyyy"
                        )
                    )
                : LocalDate
                    .now()
                    .format(
                        DateTimeFormatter.ofPattern(
                            "dd-MM-yyyy"
                        )
                    );


        return "Laudo-Neuropsicologico-"
            +
            data
            +
            "-"
            +
            normalizarNomeArquivo(
                nomePaciente
            )
            +
            ".pdf";
    }


    private String normalizarNomeArquivo(
        String valor
    ) {

        return valor
            .trim()
            .replaceAll(
                "[\\\\/:*?\"<>|]",
                ""
            )
            .replaceAll(
                "\\s+",
                "_"
            );
    }


    private void validarRequest(
        LaudoVisualizarRequest request
    ) {

        if (
            request == null
        ) {

            throw new RuntimeException(
                "Os dados para visualização do laudo são obrigatórios."
            );
        }


        if (
            request.laudoId() == null
        ) {

            throw new RuntimeException(
                "O ID do laudo é obrigatório."
            );
        }
    }


    private String valor(
        String valor
    ) {

        return valor == null
            ? ""
            : valor;
    }
}