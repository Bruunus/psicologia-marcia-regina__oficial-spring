package br.com.psicologia.marcia.service.laudo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.DTO.laudo.DocumentoLaudoGeradoDTO;
import br.com.psicologia.marcia.DTO.laudo.LaudoDownloadRequest;
import br.com.psicologia.marcia.model.Laudo;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.Psicologo;
import br.com.psicologia.marcia.model.enums.TipoPapelDocumento;
import br.com.psicologia.marcia.repository.laudo.LaudoRepository;
import br.com.psicologia.marcia.service.documento.Cabecalho;
import br.com.psicologia.marcia.service.documento.ConfiguracaoDocumento;
import br.com.psicologia.marcia.service.documento.DocumentoLayoutConfig;
import br.com.psicologia.marcia.service.documento.Rodape;
import br.com.psicologia.marcia.service.psicologo.PsicologoService;

@Service
public class DocumentoLaudoService {

    private static final String COR_SUBTITULO = "4472c4";
    private static final String COR_CABECALHO_TABELA = "8EAADB";
    private static final String COR_BORDA_TABELA = "000000";

    private static final int TAMANHO_FONTE_PADRAO = 11;
    private static final int TAMANHO_FONTE_SUBTITULO = 12; 
    private static final int TAMANHO_FONTE_TABELA = 9;
    private static final int TAMANHO_FONTE_LEGENDA = 8;

    private static final int LARGURA_TOTAL_TABELA = 9300;

    private final LaudoRepository laudoRepository;
    private final PsicologoService psicologoService;
    private final ConfiguracaoDocumento configuracaoDocumento;
    private final DocumentoLayoutConfig documentoLayoutConfig;
    private final Cabecalho cabecalho;
    private final Rodape rodape;

    public DocumentoLaudoService(
        LaudoRepository laudoRepository,
        PsicologoService psicologoService,
        ConfiguracaoDocumento configuracaoDocumento,
        DocumentoLayoutConfig documentoLayoutConfig,
        Cabecalho cabecalho,
        Rodape rodape
    ) {
        this.laudoRepository = laudoRepository;
        this.psicologoService = psicologoService;
        this.configuracaoDocumento = configuracaoDocumento;
        this.documentoLayoutConfig = documentoLayoutConfig;
        this.cabecalho = cabecalho;
        this.rodape = rodape;
    }

    @Transactional(readOnly = true)
    public DocumentoLaudoGeradoDTO gerarDocumento(
        LaudoDownloadRequest request
    ) {
        validarRequest(request);

        Laudo laudo =
            laudoRepository
                .findByIdAndStatusDeleteFalse(request.laudoId())
                .orElseThrow(
                    () -> new RuntimeException(
                        "Laudo psicológico não encontrado."
                    )
                );

        Paciente paciente = laudo.getPaciente();

        if (paciente == null) {
            throw new RuntimeException(
                "Paciente não encontrado para o laudo informado."
            );
        }

        Psicologo psicologo =
            psicologoService.buscarPsicologoAtivoPrincipal();

        TipoPapelDocumento tipoPapel =
            normalizarTipoPapel(request.tipoPapel());

        try (
            XWPFDocument documento = new XWPFDocument();
            ByteArrayOutputStream saida = new ByteArrayOutputStream()
        ) {
            configuracaoDocumento.configurar(
                documento,
                tipoPapel
            );

            cabecalho.criar(
                documento,
                psicologo,
                tipoPapel
            );

            rodape.criar(
                documento,
                psicologo
            );

            criarCorpoDocumento(
                documento,
                paciente,
                laudo,
                psicologo,
                request
            );

            documento.write(saida);

            return new DocumentoLaudoGeradoDTO(
                saida.toByteArray(),
                montarNomeArquivo(paciente, laudo),
                request.incluirEficienciaIntelectual(),
                request.incluirEscalaWasi(),
                request.incluirEscalaSrs2()
            );

        } catch (IOException e) {
            throw new RuntimeException(
                "Erro ao gerar o laudo neuropsicológico.",
                e
            );
        }
    }

    private void criarCorpoDocumento(
        XWPFDocument documento,
        Paciente paciente,
        Laudo laudo,
        Psicologo psicologo,
        LaudoDownloadRequest request
    ) {
        criarEspaco(documento, 2);

        criarTituloPrincipal(documento);

        criarSubtitulo(
            documento,
            "Identificação"
        );

        criarIdentificacao(
            documento,
            paciente,
            laudo
        );

        criarEspaco(documento, 1);

        criarSubtitulo(
            documento,
            "Descrição da demanda"
        );

        criarParagrafoCorpo(
            documento,
            laudo.getQueixa()
        );

        criarEspaco(documento, 1);

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

        criarEspaco(documento, 1);

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

        criarEspaco(documento, 1);

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

        /*
         * OPCIONAL:
         * Eficiência intelectual
         */
        if (
            Boolean.TRUE.equals(
                request.incluirEficienciaIntelectual()
            )
        ) {
            criarSecaoEficienciaIntelectual(
                documento,
                laudo
            );
        }

        /*
         * OPCIONAL:
         * Escala de Inteligência WASI
         */
        if (
            Boolean.TRUE.equals(
                request.incluirEscalaWasi()
            )
        ) {
            criarSecaoEscalaWasi(
                documento
            );
        }

        /*
         * SEÇÕES FIXAS
         */
        criarSecaoFuncoesExecutivasAtencionais(
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

        /*
         * OPCIONAL:
         * Escala de Responsividade Social - SRS-2
         */
        if (
            Boolean.TRUE.equals(
                request.incluirEscalaSrs2()
            )
        ) {
            criarSecaoSrs2(
                documento
            );
        }

        criarEspaco(documento, 1);

        criarFilhoSubtitulo(
            documento,
            "Conclusão"
        );

        criarParagrafoCorpo(
            documento,
            laudo.getConclusao()
        );

        criarEspaco(documento, 1);

        criarDataAtual(
            documento
        );

        criarEspaco(documento, 4);

        criarAssinatura(
            documento,
            psicologo
        );
    }

    private void criarTituloPrincipal(
        XWPFDocument documento
    ) {
        XWPFParagraph tituloLinhaUm =
            documento.createParagraph();

        tituloLinhaUm.setAlignment(
            ParagraphAlignment.CENTER
        );

        tituloLinhaUm.setSpacingBefore(180);
        tituloLinhaUm.setSpacingAfter(0);

        manterComProximo(
            tituloLinhaUm
        );

        XWPFRun runLinhaUm =
            tituloLinhaUm.createRun();

        aplicarFontePadrao(
            runLinhaUm
        );

        runLinhaUm.setBold(true);
        runLinhaUm.setFontSize(14);

        runLinhaUm.setText(
            "Laudo Psicológico"
        );

        XWPFParagraph tituloLinhaDois =
            documento.createParagraph();

        tituloLinhaDois.setAlignment(
            ParagraphAlignment.CENTER
        );

        tituloLinhaDois.setSpacingBefore(0);
        tituloLinhaDois.setSpacingAfter(1300);

        manterComProximo(
            tituloLinhaDois
        );

        XWPFRun runLinhaDois =
            tituloLinhaDois.createRun();

        aplicarFontePadrao(
            runLinhaDois
        );

        runLinhaDois.setBold(true);
        runLinhaDois.setFontSize(11);

        runLinhaDois.setText(
            "Com enfoque neuropsicológico"
        );
    }

    private void criarIdentificacao(
        XWPFDocument documento,
        Paciente paciente,
        Laudo laudo
    ) {
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
        XWPFDocument documento,
        String rotulo,
        String conteudo
    ) {
        XWPFParagraph paragrafo =
            documento.createParagraph();

        paragrafo.setAlignment(
            ParagraphAlignment.LEFT
        );

        paragrafo.setSpacingBefore(0);
        paragrafo.setSpacingAfter(70);

        XWPFRun runRotulo =
            paragrafo.createRun();

        aplicarFontePadrao(
            runRotulo
        );

        runRotulo.setBold(true);
        runRotulo.setText(rotulo);

        XWPFRun runConteudo =
            paragrafo.createRun();

        aplicarFontePadrao(
            runConteudo
        );

        runConteudo.setText(
            valor(conteudo)
        );
    }

    private void criarInformacoesPreliminares(
        XWPFDocument documento,
        Paciente paciente,
        Laudo laudo
    ) {
        StringBuilder texto =
            new StringBuilder();

        texto.append(
            "Este relatório foi elaborado a partir das circunstâncias do momento atual para fins clínicos e os resultados aqui descritos são influenciados por variáveis fisiológicas, ambientais e culturais. Todas as informações de anamnese foram obtidas por "
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

            if (!responsavel.isBlank()) {
                texto.append(
                    " e responsável "
                );

                texto.append(
                    responsavel
                );
            }
        }

        texto.append(".");

        criarParagrafoCorpo(
            documento,
            texto.toString()
        );
    }

    private void criarSecaoEficienciaIntelectual(
        XWPFDocument documento,
        Laudo laudo
    ) {
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
            "A integração de várias funções para resolução de problemas, mediadas pelo pensamento, linguagem, conceituação e abstração fazem parte da inteligência global. As escalas Wechsler podem ser entendidas como instrumentos que avaliam a inteligência global, bem como dois tipos de inteligência: a cristalizada, relacionada aos subtestes Vocabulário e Compreensão, e a fluida, relacionada aos subtestes Cubos e Raciocínio Matricial. Importante ressaltar que o conceito de QI possui importância diagnóstica, uma vez que se acentuam os diversos componentes da cognição."
        );

        criarEspaco(
            documento,
            1
        );
    }

    private void criarSecaoEscalaWasi(
        XWPFDocument documento
    ) {
        criarFilhoSubtitulo(
            documento,
            "Escala de Inteligência WASI"
        );

        XWPFTable tabela =
            documento.createTable(
                5,
                4
            );

        configurarTabela(
            tabela,
            new int[]{
                1900,
                2300,
                3200,
                1900
            }
        );

        configurarCabecalhoTabela(
            tabela.getRow(0),
            new String[]{
                "Índices",
                "Pontos compostos",
                "Intervalo de confiança 95%",
                "Classificação"
            }
        );

        configurarLinhasVazias(
            tabela,
            1,
            4
        );

        criarLegenda(
            documento,
            "Valores de QI: Muito abaixo da média (≤79); Médio inferior (80-89); Médio (90-109); Médio superior (110-119); Superior (120-129); Muito superior (≥130)."
        );

        criarEspaco(
            documento,
            1
        );
    }

    private void criarSecaoFuncoesExecutivasAtencionais(
        XWPFDocument documento
    ) {
        criarFilhoSubtitulo(
            documento,
            "Funções Executivas e Atencionais"
        );

        criarParagrafoCorpo(
            documento,
            "Funções executivas envolvem habilidades cognitivas e princípios de organizações necessárias para lidar com planejamento e execução de atividades, incluindo iniciação em tarefas, memória operacional, alternância, flexibilidade e controle inibitório. A atenção é o processo mediante o qual se diferenciam aqueles estímulos que serão utilizados para alcançar ou atingir determinados objetivos ou metas, enquanto são ignorados aqueles que podem ocasionar algum tipo de interferência. Os estímulos relevantes são os considerados alvo e os que produzem interferência são os chamados distratores."
        );

        XWPFTable tabela =
            documento.createTable(
                5,
                2
            );

        configurarTabela(
            tabela,
            new int[]{
                4650,
                4650
            }
        );

        configurarCabecalhoTabela(
            tabela.getRow(0),
            new String[]{
                "Função",
                "Classificação"
            }
        );

        configurarLinhasVazias(
            tabela,
            1,
            4
        );

        criarLegendaComQuebras(
            documento,
            "WECHSLER, David et al. Escala Wechsler abreviada de inteligência – WASI. 1ª. Edição. São Paulo: Casa do Psicólogo, 2014.\n"
                + "REY, André; et al. Figuras complexas de Rey: teste de cópia e de reprodução de memória de figuras geométricas complexas. São Paulo: Casa do Psicólogo, 2014.\n\n"
                + "SEDÓ, M.; PAULA, J. Malloy-Diniz. Teste dos Cinco Dígitos. Versão Brasileira. Tradução Oto Mendonça, 2015."
        );

        criarEspaco(
            documento,
            1
        );
    }

    private void criarSecaoMemoriaVisualAuditivoVerbal(
        XWPFDocument documento
    ) {
        criarFilhoSubtitulo(
            documento,
            "Memória visual e/ou auditivo-verbal"
        );

        criarParagrafoCorpo(
            documento,
            "Memória é a habilidade de adquirir, reter e evocar informações. É composta por subsistemas e subdividida em diferentes tipos."
        );

        XWPFTable tabela =
            documento.createTable(
                5,
                2
            );

        configurarTabela(
            tabela,
            new int[]{
                4650,
                4650
            }
        );

        configurarCabecalhoTabela(
            tabela.getRow(0),
            new String[]{
                "Função",
                "Classificação"
            }
        );

        configurarLinhasVazias(
            tabela,
            1,
            4
        );

        criarEspaco(
            documento,
            1
        );
    }

    private void criarSecaoMemoriaAprendizagem(
        XWPFDocument documento
    ) {
        criarFilhoSubtitulo(
            documento,
            "Memória e aprendizagem"
        );

        XWPFTable tabela =
            documento.createTable(
                11,
                5
            );

        configurarTabela(
            tabela,
            new int[]{
                2800,
                1625,
                1625,
                1625,
                1625
            }
        );

        configurarCabecalhoMemoriaAprendizagem(
            tabela.getRow(0)
        );

        definirTextoCelula(
            tabela.getRow(1).getCell(0),
            "Primeiras etapas da aprendizagem",
            true,
            ParagraphAlignment.CENTER,
            TAMANHO_FONTE_TABELA
        );

        mesclarVerticalmente(
            tabela,
            0,
            1,
            5
        );

        for (
            int linha = 1;
            linha <= 5;
            linha++
        ) {
            configurarCelulasResultadoVazias(
                tabela.getRow(linha),
                1,
                4
            );
        }

        configurarLinhaRotuladaMemoria(
            tabela.getRow(6),
            "Distrator"
        );

        configurarLinhaRotuladaMemoria(
            tabela.getRow(7),
            "Evocação Imediata"
        );

        configurarLinhaRotuladaMemoria(
            tabela.getRow(8),
            "Evocação Tardia"
        );

        configurarLinhaRotuladaMemoria(
            tabela.getRow(9),
            "Reconhecimento"
        );

        configurarLinhaRotuladaMemoria(
            tabela.getRow(10),
            "Índice de aprendizagem"
        );

        criarEspaco(
            documento,
            1
        );
    }

    private void criarSecaoFuncoesVisoespaciais(
        XWPFDocument documento
    ) {
        criarFilhoSubtitulo(
            documento,
            "Funções viso-espaciais/praxias viso-construtivas"
        );

        criarParagrafoCorpo(
            documento,
            "Habilidades que são mediadas por funções visuais e que permitem executar ações voltadas a um fim num plano concreto por meio da atividade motora."
        );

        XWPFTable tabela =
            documento.createTable(
                5,
                2
            );

        configurarTabela(
            tabela,
            new int[]{
                4650,
                4650
            }
        );

        configurarCabecalhoTabela(
            tabela.getRow(0),
            new String[]{
                "Função",
                "Classificação"
            }
        );

        configurarLinhasVazias(
            tabela,
            1,
            4
        );

        criarEspaco(
            documento,
            1
        );
    }

    private void criarSecaoSrs2(
        XWPFDocument documento
    ) {
        criarFilhoSubtitulo(
            documento,
            "Escala de Responsividade Social – SRS-2"
        );

        criarParagrafoCorpo(
            documento,
            "A Escala de Responsividade Social, Segunda Edição (SRS-2) tem como objetivo mensurar sintomas associados ao Transtorno do Espectro Autista (TEA), bem como classificá-los em níveis leves, moderados ou severos. Sua avaliação se faz de forma global e específica, já que agrupa os sintomas em subcategorias, incluindo Escalas Compatíveis ao DSM-5 e Subescalas de Intervenção. Por esse motivo, pode ser utilizada para iniciar processos diagnósticos, como rastreio, e para o planejamento de intervenções clínicas e ocupacionais."
        );

        XWPFTable tabela =
            documento.createTable(
                5,
                3
            );

        configurarTabela(
            tabela,
            new int[]{
                3100,
                3100,
                3100
            }
        );

        configurarCabecalhoTabela(
            tabela.getRow(0),
            new String[]{
                "Função",
                "Autorrelato",
                "Heterorrelato"
            }
        );

        configurarLinhasVazias(
            tabela,
            1,
            4
        );

        criarEspaco(
            documento,
            1
        );
    }

    private void configurarTabela(
        XWPFTable tabela,
        int[] larguras
    ) {
        tabela.setCellMargins(
            50,
            70,
            50,
            70
        );

        CTTblPr tblPr =
            tabela.getCTTbl().getTblPr();

        if (tblPr == null) {
            tblPr =
                tabela
                    .getCTTbl()
                    .addNewTblPr();
        }

        CTTblWidth larguraTabela =
            tblPr.getTblW();

        if (larguraTabela == null) {
            larguraTabela =
                tblPr.addNewTblW();
        }

        larguraTabela.setType(
            STTblWidth.DXA
        );

        larguraTabela.setW(
            BigInteger.valueOf(
                LARGURA_TOTAL_TABELA
            )
        );

        configurarBordasTabela(
            tabela
        );

        for (
            XWPFTableRow linha :
            tabela.getRows()
        ) {
            linha.setHeight(500);

            for (
                int coluna = 0;
                coluna < linha.getTableCells().size();
                coluna++
            ) {
                int largura =
                    coluna < larguras.length
                        ? larguras[coluna]
                        : larguras[
                            larguras.length - 1
                        ];

                definirLarguraCelula(
                    linha.getCell(coluna),
                    largura
                );

                linha
                    .getCell(coluna)
                    .setVerticalAlignment(
                        XWPFTableCell
                            .XWPFVertAlign
                            .CENTER
                    );
            }
        }
    }

    private void configurarCabecalhoTabela(
        XWPFTableRow linha,
        String[] textos
    ) {
        linha.setHeight(600);

        for (
            int coluna = 0;
            coluna < textos.length;
            coluna++
        ) {
            XWPFTableCell celula =
                linha.getCell(coluna);

            aplicarCorFundoCelula(
                celula,
                COR_CABECALHO_TABELA
            );

            definirTextoCelula(
                celula,
                textos[coluna],
                true,
                ParagraphAlignment.CENTER,
                TAMANHO_FONTE_TABELA
            );
        }
    }

    private void configurarCabecalhoMemoriaAprendizagem(
        XWPFTableRow linha
    ) {
        definirTextoCelula(
            linha.getCell(0),
            "",
            false,
            ParagraphAlignment.CENTER,
            TAMANHO_FONTE_TABELA
        );

        String[] textos = {
            "categorias",
            "pontuação",
            "percentil",
            "classificação"
        };

        for (
            int coluna = 1;
            coluna <= 4;
            coluna++
        ) {
            XWPFTableCell celula =
                linha.getCell(coluna);

            aplicarCorFundoCelula(
                celula,
                COR_CABECALHO_TABELA
            );

            definirTextoCelula(
                celula,
                textos[coluna - 1],
                true,
                ParagraphAlignment.CENTER,
                TAMANHO_FONTE_TABELA
            );
        }
    }

    private void configurarLinhaRotuladaMemoria(
        XWPFTableRow linha,
        String rotulo
    ) {
        linha.setHeight(500);

        definirTextoCelula(
            linha.getCell(0),
            rotulo,
            true,
            ParagraphAlignment.CENTER,
            TAMANHO_FONTE_TABELA
        );

        configurarCelulasResultadoVazias(
            linha,
            1,
            4
        );
    }

    private void configurarCelulasResultadoVazias(
        XWPFTableRow linha,
        int colunaInicial,
        int colunaFinal
    ) {
        for (
            int coluna = colunaInicial;
            coluna <= colunaFinal;
            coluna++
        ) {
            definirTextoCelula(
                linha.getCell(coluna),
                "",
                false,
                ParagraphAlignment.LEFT,
                TAMANHO_FONTE_TABELA
            );
        }
    }

    private void configurarLinhasVazias(
        XWPFTable tabela,
        int linhaInicial,
        int quantidade
    ) {
        int linhaFinal =
            linhaInicial + quantidade;

        for (
            int linha = linhaInicial;
            linha < linhaFinal;
            linha++
        ) {
            XWPFTableRow row =
                tabela.getRow(linha);

            row.setHeight(500);

            for (
                XWPFTableCell celula :
                row.getTableCells()
            ) {
                definirTextoCelula(
                    celula,
                    "",
                    false,
                    ParagraphAlignment.LEFT,
                    TAMANHO_FONTE_TABELA
                );
            }
        }
    }

    private void definirTextoCelula(
        XWPFTableCell celula,
        String texto,
        boolean negrito,
        ParagraphAlignment alinhamento,
        int tamanhoFonte
    ) {
        limparParagrafosCelula(
            celula
        );

        XWPFParagraph paragrafo =
            celula.addParagraph();

        paragrafo.setAlignment(
            alinhamento
        );

        paragrafo.setSpacingBefore(0);
        paragrafo.setSpacingAfter(0);

        XWPFRun run =
            paragrafo.createRun();

        run.setFontFamily(
            documentoLayoutConfig
                .fonteTexto()
        );

        run.setFontSize(
            tamanhoFonte
        );

        run.setBold(
            negrito
        );

        run.setText(
            valor(texto)
        );
    }

    private void limparParagrafosCelula(
        XWPFTableCell celula
    ) {
        while (
            celula.getParagraphs().size() > 0
        ) {
            celula.removeParagraph(0);
        }
    }

    private void aplicarCorFundoCelula(
        XWPFTableCell celula,
        String cor
    ) {
        CTTcPr tcPr =
            obterPropriedadesCelula(
                celula
            );

        CTShd shd =
            tcPr.isSetShd()
                ? tcPr.getShd()
                : tcPr.addNewShd();

        shd.setVal(
            STShd.CLEAR
        );

        shd.setColor("auto");
        shd.setFill(cor);
    }

    private void definirLarguraCelula(
        XWPFTableCell celula,
        int larguraTwips
    ) {
        CTTcPr tcPr =
            obterPropriedadesCelula(
                celula
            );

        CTTblWidth largura =
            tcPr.getTcW();

        if (largura == null) {
            largura =
                tcPr.addNewTcW();
        }

        largura.setType(
            STTblWidth.DXA
        );

        largura.setW(
            BigInteger.valueOf(
                larguraTwips
            )
        );
    }

    private CTTcPr obterPropriedadesCelula(
        XWPFTableCell celula
    ) {
        CTTcPr tcPr =
            celula
                .getCTTc()
                .getTcPr();

        if (tcPr == null) {
            tcPr =
                celula
                    .getCTTc()
                    .addNewTcPr();
        }

        return tcPr;
    }

    private void mesclarVerticalmente(
        XWPFTable tabela,
        int coluna,
        int linhaInicial,
        int linhaFinal
    ) {
        for (
            int linha = linhaInicial;
            linha <= linhaFinal;
            linha++
        ) {
            XWPFTableCell celula =
                tabela
                    .getRow(linha)
                    .getCell(coluna);

            CTTcPr tcPr =
                obterPropriedadesCelula(
                    celula
                );

            CTVMerge merge =
                tcPr.isSetVMerge()
                    ? tcPr.getVMerge()
                    : tcPr.addNewVMerge();

            if (linha == linhaInicial) {
                merge.setVal(
                    STMerge.RESTART
                );
            } else {
                merge.setVal(
                    STMerge.CONTINUE
                );
            }
        }
    }

    private void configurarBordasTabela(
        XWPFTable tabela
    ) {
        CTTblPr tblPr =
            tabela
                .getCTTbl()
                .getTblPr();

        if (tblPr == null) {
            tblPr =
                tabela
                    .getCTTbl()
                    .addNewTblPr();
        }

        CTTblBorders bordas =
            tblPr.getTblBorders();

        if (bordas == null) {
            bordas =
                tblPr.addNewTblBorders();
        }

        configurarBorda(
            bordas.isSetTop()
                ? bordas.getTop()
                : bordas.addNewTop()
        );

        configurarBorda(
            bordas.isSetBottom()
                ? bordas.getBottom()
                : bordas.addNewBottom()
        );

        configurarBorda(
            bordas.isSetLeft()
                ? bordas.getLeft()
                : bordas.addNewLeft()
        );

        configurarBorda(
            bordas.isSetRight()
                ? bordas.getRight()
                : bordas.addNewRight()
        );

        configurarBorda(
            bordas.isSetInsideH()
                ? bordas.getInsideH()
                : bordas.addNewInsideH()
        );

        configurarBorda(
            bordas.isSetInsideV()
                ? bordas.getInsideV()
                : bordas.addNewInsideV()
        );
    }

    private void configurarBorda(
        CTBorder borda
    ) {
        borda.setVal(
            STBorder.SINGLE
        );

        borda.setSz(
            BigInteger.valueOf(6)
        );

        borda.setSpace(
            BigInteger.ZERO
        );

        borda.setColor(
            COR_BORDA_TABELA
        );
    }

    private void criarSubtitulo(
        XWPFDocument documento,
        String texto
    ) {
        XWPFParagraph paragrafo =
            documento.createParagraph();

        paragrafo.setAlignment(
            ParagraphAlignment.LEFT
        );

        paragrafo.setSpacingBefore(200);
        paragrafo.setSpacingAfter(400);

        manterComProximo(
            paragrafo
        );

        XWPFRun run =
            paragrafo.createRun();

        run.setFontFamily(
            "Arial"
        );

        run.setFontSize(
            TAMANHO_FONTE_SUBTITULO
        );

        run.setBold(true);

        run.setColor(
            COR_SUBTITULO
        );

        run.setText(
            valor(texto)
        );
    }

    private void criarFilhoSubtitulo(
        XWPFDocument documento,
        String texto
    ) {
        XWPFParagraph paragrafo =
            documento.createParagraph();

        paragrafo.setAlignment(
            ParagraphAlignment.LEFT
        );

        paragrafo.setSpacingBefore(140);
        paragrafo.setSpacingAfter(80);

        manterComProximo(
            paragrafo
        );

        XWPFRun run =
            paragrafo.createRun();

        aplicarFontePadrao(
            run
        );

        run.setBold(true);

        run.setText(
            valor(texto)
        );
    }

    private void criarParagrafoCorpo(
        XWPFDocument documento,
        String texto
    ) {
        if (
            texto == null ||
            texto.isBlank()
        ) {
            return;
        }

        XWPFParagraph paragrafo =
            documento.createParagraph();

        paragrafo.setAlignment(
            ParagraphAlignment.BOTH
        );

        paragrafo.setIndentationFirstLine(
            documentoLayoutConfig
                .recuoPrimeiraLinhaCorpoTwips()
        );

        paragrafo.setSpacingBefore(80);
        paragrafo.setSpacingAfter(160);

        XWPFRun run =
            paragrafo.createRun();

        aplicarFontePadrao(
            run
        );

        adicionarTextoComQuebras(
            run,
            texto
        );
    }

    private void criarLegenda(
        XWPFDocument documento,
        String texto
    ) {
        XWPFParagraph paragrafo =
            documento.createParagraph();

        paragrafo.setAlignment(
            ParagraphAlignment.LEFT
        );

        paragrafo.setSpacingBefore(40);
        paragrafo.setSpacingAfter(80);

        XWPFRun run =
            paragrafo.createRun();

        run.setFontFamily(
            documentoLayoutConfig
                .fonteTexto()
        );

        run.setFontSize(
            TAMANHO_FONTE_LEGENDA
        );

        run.setText(
            valor(texto)
        );
    }

    private void criarLegendaComQuebras(
        XWPFDocument documento,
        String texto
    ) {
        XWPFParagraph paragrafo =
            documento.createParagraph();

        paragrafo.setAlignment(
            ParagraphAlignment.LEFT
        );

        paragrafo.setSpacingBefore(80);
        paragrafo.setSpacingAfter(80);

        XWPFRun run =
            paragrafo.createRun();

        run.setFontFamily(
            documentoLayoutConfig
                .fonteTexto()
        );

        run.setFontSize(
            TAMANHO_FONTE_LEGENDA
        );

        adicionarTextoComQuebras(
            run,
            texto
        );
    }

    private void criarDataAtual(
        XWPFDocument documento
    ) {
        LocalDate dataAtual =
            LocalDate.now();

        Locale locale =
            Locale.forLanguageTag(
                "pt-BR"
            );

        String mes =
            dataAtual
                .getMonth()
                .getDisplayName(
                    TextStyle.FULL,
                    locale
                );

        String texto =
            "São Paulo, "
                + dataAtual.getDayOfMonth()
                + " de "
                + mes
                + " de "
                + dataAtual.getYear()
                + ".";

        XWPFParagraph paragrafo =
            documento.createParagraph();

        paragrafo.setAlignment(
            ParagraphAlignment.LEFT
        );

        paragrafo.setSpacingBefore(200);
        paragrafo.setSpacingAfter(200);

        XWPFRun run =
            paragrafo.createRun();

        aplicarFontePadrao(
            run
        );

        run.setText(
            texto
        );
    }

    private void criarAssinatura(
        XWPFDocument documento,
        Psicologo psicologo
    ) {
        XWPFParagraph linha =
            criarParagrafoAssinatura(
                documento
            );

        manterComProximo(
            linha
        );

        XWPFRun linhaRun =
            linha.createRun();

        aplicarFontePadrao(
            linhaRun
        );

        linhaRun.setText(
            "_____________________________"
        );

        XWPFParagraph nome =
            criarParagrafoAssinatura(
                documento
            );

        manterComProximo(
            nome
        );

        XWPFRun nomeRun =
            nome.createRun();

        aplicarFontePadrao(
            nomeRun
        );

        nomeRun.setText(
            valor(
                psicologo.getNome()
            )
        );

        XWPFParagraph funcao =
            criarParagrafoAssinatura(
                documento
            );

        XWPFRun funcaoRun =
            funcao.createRun();

        aplicarFontePadrao(
            funcaoRun
        );

        funcaoRun.setText(
            "Psicóloga CRP: "
                + valor(
                    psicologo.getCrp()
                )
        );
    }

    private XWPFParagraph criarParagrafoAssinatura(
        XWPFDocument documento
    ) {
        XWPFParagraph paragrafo =
            documento.createParagraph();

        paragrafo.setAlignment(
            ParagraphAlignment.CENTER
        );

        paragrafo.setSpacingBefore(0);
        paragrafo.setSpacingAfter(50);

        return paragrafo;
    }

    private void aplicarFontePadrao(
        XWPFRun run
    ) {
        run.setFontFamily(
            documentoLayoutConfig
                .fonteTexto()
        );

        run.setFontSize(
            TAMANHO_FONTE_PADRAO
        );
    }

    private void adicionarTextoComQuebras(
        XWPFRun run,
        String texto
    ) {
        String[] linhas =
            valor(texto)
                .split("\\R", -1);

        for (
            int indice = 0;
            indice < linhas.length;
            indice++
        ) {
            if (indice > 0) {
                run.addBreak();
            }

            run.setText(
                linhas[indice]
            );
        }
    }

    private void criarEspaco(
        XWPFDocument documento,
        int quantidade
    ) {
        for (
            int indice = 0;
            indice < quantidade;
            indice++
        ) {
            XWPFParagraph paragrafo =
                documento.createParagraph();

            paragrafo.setSpacingBefore(0);
            paragrafo.setSpacingAfter(0);

            XWPFRun run =
                paragrafo.createRun();

            run.addBreak();
        }
    }

    private void manterComProximo(
        XWPFParagraph paragrafo
    ) {
        CTPPr pPr =
            paragrafo
                .getCTP()
                .isSetPPr()
                ? paragrafo
                    .getCTP()
                    .getPPr()
                : paragrafo
                    .getCTP()
                    .addNewPPr();

        if (!pPr.isSetKeepNext()) {
            pPr.addNewKeepNext();
        }
    }

    private boolean pacienteMenorDeIdade(
        Paciente paciente,
        Laudo laudo
    ) {
        if (
            paciente.getDataNascimento()
                == null
        ) {
            return false;
        }

        LocalDate dataReferencia =
            laudo.getPeriodoAvaliacaoInicio()
                != null
                ? laudo
                    .getPeriodoAvaliacaoInicio()
                : LocalDate.now();

        int idade =
            Period.between(
                paciente.getDataNascimento(),
                dataReferencia
            ).getYears();

        return idade < 18;
    }

    private String formatarIdadeNaAvaliacao(
        Paciente paciente,
        Laudo laudo
    ) {
        if (
            paciente.getDataNascimento()
                == null
        ) {
            return "";
        }

        LocalDate dataReferencia =
            laudo.getPeriodoAvaliacaoInicio()
                != null
                ? laudo
                    .getPeriodoAvaliacaoInicio()
                : LocalDate.now();

        int idade =
            Period.between(
                paciente.getDataNascimento(),
                dataReferencia
            ).getYears();

        return idade + " anos";
    }

    private void criarParagrafoProcedimentos(
        XWPFDocument documento,
        Laudo laudo
    ) {
        String quantidadeSessoes =
            laudo.getQuantidadeSessoes()
                == null
                ? ""
                : String.valueOf(
                    laudo.getQuantidadeSessoes()
                );

        String texto =
            "Foram realizadas "
                + quantidadeSessoes
                + " sessões no período de "
                + valor(
                    laudo.getPeriodo()
                )
                + ", com "
                + formatarDuracao(
                    laudo.getDuracaoMinutos()
                )
                + " de duração.";

        criarParagrafoCorpo(
            documento,
            texto
        );
    }

    private String formatarDuracao(
        Integer duracaoMinutos
    ) {
        if (
            duracaoMinutos == null ||
            duracaoMinutos <= 0
        ) {
            return "1 hora";
        }

        int horas =
            duracaoMinutos / 60;

        int minutos =
            duracaoMinutos % 60;

        if (horas == 0) {
            return minutos == 1
                ? "1 minuto"
                : minutos + " minutos";
        }

        String textoHoras =
            horas == 1
                ? "1 hora"
                : horas + " horas";

        if (minutos == 0) {
            return textoHoras;
        }

        String textoMinutos =
            minutos == 1
                ? "1 minuto"
                : minutos + " minutos";

        return textoHoras
            + " e "
            + textoMinutos;
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
            inicio.isBlank() &&
            fim.isBlank()
        ) {
            return "";
        }

        if (inicio.isBlank()) {
            return fim;
        }

        if (fim.isBlank()) {
            return inicio;
        }

        return inicio
            + " à "
            + fim;
    }

    private String formatarData(
        LocalDate data
    ) {
        if (data == null) {
            return "";
        }

        return data.format(
            DateTimeFormatter.ofPattern(
                "dd/MM/yyyy"
            )
        );
    }

    private void validarRequest(
        LaudoDownloadRequest request
    ) {
        if (request == null) {
            throw new RuntimeException(
                "Os dados para geração do documento são obrigatórios."
            );
        }

        if (request.laudoId() == null) {
            throw new RuntimeException(
                "O ID do laudo é obrigatório."
            );
        }
    }

    private TipoPapelDocumento normalizarTipoPapel(
        String tipoPapel
    ) {
        if (
            tipoPapel == null ||
            tipoPapel.isBlank()
        ) {
            return TipoPapelDocumento.A4;
        }

        try {
            return TipoPapelDocumento.valueOf(
                tipoPapel
                    .trim()
                    .toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                "Tipo de papel inválido. Use A4 ou CARTA."
            );
        }
    }

    private String montarNomeArquivo(
        Paciente paciente,
        Laudo laudo
    ) {
        String nomePaciente =
            paciente.getNomeCompleto() != null
                && !paciente
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
            + data
            + "-"
            + normalizarNomeArquivo(
                nomePaciente
            )
            + ".docx";
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

    private String valor(
        String valor
    ) {
        return valor == null
            ? ""
            : valor;
    }
}