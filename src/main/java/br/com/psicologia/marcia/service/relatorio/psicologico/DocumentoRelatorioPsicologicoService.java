package br.com.psicologia.marcia.service.relatorio.psicologico;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.DTO.relatorio.psicologico.DocumentoRelatorioPsicologicoGeradoDTO;
import br.com.psicologia.marcia.DTO.relatorio.psicologico.RelatorioPsicologicoDownloadRequest;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.Psicologo;
import br.com.psicologia.marcia.model.RelatorioPsicologico;
import br.com.psicologia.marcia.model.RelatorioPsicologicoHipoteseDiagnostica;
import br.com.psicologia.marcia.model.enums.TipoPapelDocumento;
import br.com.psicologia.marcia.repository.relatorio.psicologico.RelatorioPsicologicoRepository;
import br.com.psicologia.marcia.service.documento.BlocoIdentificacaoDocumentoService;
import br.com.psicologia.marcia.service.documento.Cabecalho;
import br.com.psicologia.marcia.service.documento.ConfiguracaoDocumento;
import br.com.psicologia.marcia.service.documento.DocumentoLayoutConfig;
import br.com.psicologia.marcia.service.documento.Rodape;
import br.com.psicologia.marcia.service.psicologo.PsicologoService;

@Service
public class DocumentoRelatorioPsicologicoService {

    private final RelatorioPsicologicoRepository relatorioPsicologicoRepository;
    private final PsicologoService psicologoService;
    private final Cabecalho cabecalho;
    private final Rodape rodape;
    private final ConfiguracaoDocumento configuracaoDocumento;
    private final DocumentoLayoutConfig documentoLayoutConfig;
    private final BlocoIdentificacaoDocumentoService blocoIdentificacaoDocumentoService;

    public DocumentoRelatorioPsicologicoService(
            RelatorioPsicologicoRepository relatorioPsicologicoRepository,
            PsicologoService psicologoService,
            Cabecalho cabecalho,
            Rodape rodape,
            ConfiguracaoDocumento configuracaoDocumento,
            DocumentoLayoutConfig documentoLayoutConfig,
            BlocoIdentificacaoDocumentoService blocoIdentificacaoDocumentoService
    ) {
        this.relatorioPsicologicoRepository = relatorioPsicologicoRepository;
        this.psicologoService = psicologoService;
        this.cabecalho = cabecalho;
        this.rodape = rodape;
        this.configuracaoDocumento = configuracaoDocumento;
        this.documentoLayoutConfig = documentoLayoutConfig;
        this.blocoIdentificacaoDocumentoService = blocoIdentificacaoDocumentoService;
    }

    @Transactional(readOnly = true)
    public DocumentoRelatorioPsicologicoGeradoDTO gerarDocumento(RelatorioPsicologicoDownloadRequest request) {
        RelatorioPsicologico relatorio = relatorioPsicologicoRepository.findById(request.relatorioId())
                .orElseThrow(() -> new RuntimeException("Relatório psicológico não encontrado."));

        Paciente paciente = relatorio.getPaciente();
        Psicologo psicologo = psicologoService.buscarPsicologoAtivoPrincipal();
        TipoPapelDocumento tipoPapel = normalizarTipoPapel(request.tipoPapel());

        try (
                XWPFDocument documento = new XWPFDocument();
                ByteArrayOutputStream saida = new ByteArrayOutputStream()
        ) {
            configuracaoDocumento.configurar(documento, tipoPapel);
            cabecalho.criar(documento, psicologo, tipoPapel);
            rodape.criar(documento);

            criarEspaco(documento, 2);
            blocoIdentificacaoDocumentoService.criarDocx(documento, paciente);
            criarEspaco(documento, 2);

            criarTitulo(documento);
            criarEspaco(documento, 1);

            int quantidadeLinhasConteudo = 0;

            quantidadeLinhasConteudo += criarPrimeiroParagrafo(documento, relatorio, tipoPapel);
            quantidadeLinhasConteudo += criarSegundoParagrafoAnalise(documento, relatorio, tipoPapel);
            quantidadeLinhasConteudo += criarTerceiroParagrafoHipoteses(documento, relatorio, tipoPapel);
            quantidadeLinhasConteudo += criarQuartoParagrafo(documento, tipoPapel);
            quantidadeLinhasConteudo += criarDataEmissao(documento, relatorio, tipoPapel);

            posicionarAssinatura(documento, psicologo, tipoPapel, quantidadeLinhasConteudo);

            documento.write(saida);

            return new DocumentoRelatorioPsicologicoGeradoDTO(
                    saida.toByteArray(),
                    montarNomeArquivo(paciente, relatorio)
            );
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar documento de relatório psicológico.", e);
        }
    }

    private void criarTitulo(XWPFDocument documento) {
        XWPFParagraph paragrafo = documento.createParagraph();

        paragrafo.setAlignment(ParagraphAlignment.CENTER);
        paragrafo.setSpacingBefore(documentoLayoutConfig.espacamentoTituloAntesTwips());
        paragrafo.setSpacingAfter(documentoLayoutConfig.espacamentoTituloDepoisTwips());

        XWPFRun run = paragrafo.createRun();

        aplicarFontePadrao(run);

        run.setBold(true);
        run.setFontSize(documentoLayoutConfig.tamanhoFonteTitulo());
        run.setText("Relatório Psicológico");
    }

    private int criarPrimeiroParagrafo(
            XWPFDocument documento,
            RelatorioPsicologico relatorio,
            TipoPapelDocumento tipoPapel
    ) {
        XWPFParagraph paragrafo = criarParagrafoCorpo(documento);

        String texto =
                "Paciente supracitado está em acompanhamento psicoterapêutico desde " +
                        montarDataAcompanhamento(relatorio) +
                        ",sobre os meus cuidados dentro da perspectiva " +
                        formatarTextoDeCampo(relatorio.getAbordagem()) +
                        ". Os atendimentos ocorreram " +
                        montarTextoOcorrencia(relatorio.getOcorrencia()) +
                        " por semana em formato " +
                        formatarTextoDeCampo(relatorio.getFormato()) +
                        ".";

        adicionarTextoNormal(paragrafo, "Paciente supracitado está em acompanhamento psicoterapêutico desde ");
        adicionarTextoNormal(paragrafo, montarDataAcompanhamento(relatorio));
        adicionarTextoNormal(paragrafo, ",sobre os meus cuidados dentro da perspectiva ");
        adicionarTextoNormal(paragrafo, formatarTextoDeCampo(relatorio.getAbordagem()));
        adicionarTextoNormal(paragrafo, ". Os atendimentos ocorreram ");
        adicionarTextoNormal(paragrafo, montarTextoOcorrencia(relatorio.getOcorrencia()));
        adicionarTextoNormal(paragrafo, " por semana em formato ");
        adicionarTextoNormal(paragrafo, formatarTextoDeCampo(relatorio.getFormato()));
        adicionarTextoNormal(paragrafo, ".");

        return contarLinhasEstimadas(texto, tipoPapel);
    }

    private int criarSegundoParagrafoAnalise(
            XWPFDocument documento,
            RelatorioPsicologico relatorio,
            TipoPapelDocumento tipoPapel
    ) {
        String textoAnalise = valor(relatorio.getAnalise());

        XWPFParagraph paragrafo = criarParagrafoCorpo(documento);

        XWPFRun run = paragrafo.createRun();

        aplicarFontePadrao(run);
        adicionarTextoComQuebras(run, textoAnalise);

        return contarLinhasEstimadas(textoAnalise, tipoPapel);
    }

    private int criarTerceiroParagrafoHipoteses(
            XWPFDocument documento,
            RelatorioPsicologico relatorio,
            TipoPapelDocumento tipoPapel
    ) {
        int quantidadeLinhas = 0;

        XWPFParagraph titulo = criarParagrafo(documento, ParagraphAlignment.LEFT);

        titulo.setSpacingBefore(documentoLayoutConfig.espacamentoAntesHipoteseTituloTwips());
        titulo.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisHipoteseTituloTwips());

        adicionarTextoNegrito(titulo, "Hipótese diagnóstica:");

        quantidadeLinhas += 1;

        List<RelatorioPsicologicoHipoteseDiagnostica> hipoteses = relatorio.getHipotesesDiagnosticas();

        if (hipoteses == null || hipoteses.isEmpty()) {
            String texto = "Nenhuma hipótese diagnóstica informada.";

            XWPFParagraph paragrafo = criarParagrafo(documento, ParagraphAlignment.LEFT);

            paragrafo.setIndentationLeft(documentoLayoutConfig.recuoHipoteseTwips());

            adicionarTextoNormal(paragrafo, texto);

            quantidadeLinhas += contarLinhasEstimadas(texto, tipoPapel);

            return quantidadeLinhas;
        }

        for (RelatorioPsicologicoHipoteseDiagnostica hipotese : hipoteses) {
            String texto = valor(hipotese.getDescricao());

            XWPFParagraph paragrafo = criarParagrafo(documento, ParagraphAlignment.LEFT);

            paragrafo.setIndentationLeft(documentoLayoutConfig.recuoHipoteseTwips());
            paragrafo.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisItemHipoteseTwips());

            adicionarTextoNormal(paragrafo, texto);

            quantidadeLinhas += contarLinhasEstimadas(texto, tipoPapel);
        }

        return quantidadeLinhas;
    }

    private int criarQuartoParagrafo(
            XWPFDocument documento,
            TipoPapelDocumento tipoPapel
    ) {
        String texto = "Sem mais para quaisquer dúvidas,estou à disposição.";

        XWPFParagraph paragrafo = criarParagrafo(documento, ParagraphAlignment.LEFT);

        paragrafo.setSpacingBefore(documentoLayoutConfig.espacamentoAntesFraseFinalTwips());
        paragrafo.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisFraseFinalTwips());

        adicionarTextoNormal(paragrafo, texto);

        return contarLinhasEstimadas(texto, tipoPapel);
    }

    private int criarDataEmissao(
            XWPFDocument documento,
            RelatorioPsicologico relatorio,
            TipoPapelDocumento tipoPapel
    ) {
        String texto =
                valor(relatorio.getCidadeEmissao()) +
                        "," +
                        valor(relatorio.getDiaEmissao()) +
                        " de " +
                        valor(relatorio.getMesEmissao()) +
                        " de " +
                        valor(relatorio.getAnoEmissao()) +
                        ".";

        XWPFParagraph paragrafo = criarParagrafo(documento, ParagraphAlignment.RIGHT);

        paragrafo.setSpacingBefore(documentoLayoutConfig.espacamentoAntesDataEmissaoTwips());
        paragrafo.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisDataEmissaoTwips());

        XWPFRun run = paragrafo.createRun();

        aplicarFontePadrao(run);
        run.setText(texto);

        return contarLinhasEstimadas(texto, tipoPapel);
    }

    private void posicionarAssinatura(
            XWPFDocument documento,
            Psicologo psicologo,
            TipoPapelDocumento tipoPapel,
            int quantidadeLinhasConteudo
    ) {
        int quantidadeEspacosAntesAssinatura = documentoLayoutConfig.quantidadeEspacosAntesAssinaturaDocx(
                tipoPapel,
                quantidadeLinhasConteudo
        );

        criarEspaco(documento, quantidadeEspacosAntesAssinatura);

        XWPFParagraph linha = documento.createParagraph();

        linha.setAlignment(ParagraphAlignment.CENTER);
        linha.setSpacingBefore(0);
        linha.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisLinhaAssinaturaTwips());

        manterComProximo(linha);

        XWPFRun linhaRun = linha.createRun();

        aplicarFontePadrao(linhaRun);
        linhaRun.setText(documentoLayoutConfig.linhaAssinatura());

        XWPFParagraph nome = documento.createParagraph();

        nome.setAlignment(ParagraphAlignment.CENTER);
        nome.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisNomeAssinaturaTwips());

        manterComProximo(nome);

        XWPFRun nomeRun = nome.createRun();

        aplicarFontePadrao(nomeRun);
        nomeRun.setText(valor(psicologo.getNome()));

        XWPFParagraph funcao = documento.createParagraph();

        funcao.setAlignment(ParagraphAlignment.CENTER);
        funcao.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisFuncaoAssinaturaTwips());

        manterComProximo(funcao);

        XWPFRun funcaoRun = funcao.createRun();

        aplicarFontePadrao(funcaoRun);
        funcaoRun.setText(valor(psicologo.getFuncaoEmpresa()));

        XWPFParagraph crp = documento.createParagraph();

        crp.setAlignment(ParagraphAlignment.CENTER);
        crp.setSpacingAfter(0);

        XWPFRun crpRun = crp.createRun();

        aplicarFontePadrao(crpRun);
        crpRun.setText("CRP: " + valor(psicologo.getCrp()));
    }

    private void manterComProximo(XWPFParagraph paragrafo) {
        CTPPr pPr = paragrafo.getCTP().isSetPPr()
                ? paragrafo.getCTP().getPPr()
                : paragrafo.getCTP().addNewPPr();

        if (!pPr.isSetKeepNext()) {
            pPr.addNewKeepNext();
        }
    }

    private int contarLinhasEstimadas(String texto, TipoPapelDocumento tipoPapel) {
        if (texto == null || texto.isBlank()) {
            return 0;
        }

        int caracteresPorLinha = documentoLayoutConfig.caracteresPorLinhaConteudoDocx(tipoPapel);
        int quantidadeLinhas = 0;

        String[] linhas = texto.split("\\R", -1);

        for (String linha : linhas) {
            quantidadeLinhas += contarLinhasEstimadasDaLinha(linha, caracteresPorLinha);
        }

        return quantidadeLinhas;
    }

    private int contarLinhasEstimadasDaLinha(String linha, int caracteresPorLinha) {
        if (linha == null || linha.isBlank()) {
            return 1;
        }

        int quantidadeCaracteres = linha.trim().length();

        return Math.max(
                1,
                (int) Math.ceil((double) quantidadeCaracteres / caracteresPorLinha)
        );
    }

    private XWPFParagraph criarParagrafoCorpo(XWPFDocument documento) {
        XWPFParagraph paragrafo = criarParagrafo(documento, ParagraphAlignment.BOTH);

        paragrafo.setIndentationFirstLine(documentoLayoutConfig.recuoPrimeiraLinhaCorpoTwips());
        paragrafo.setSpacingBefore(documentoLayoutConfig.espacamentoCorpoAntesTwips());
        paragrafo.setSpacingAfter(documentoLayoutConfig.espacamentoCorpoDepoisTwips());

        return paragrafo;
    }

    private XWPFParagraph criarParagrafo(XWPFDocument documento, ParagraphAlignment alinhamento) {
        XWPFParagraph paragrafo = documento.createParagraph();

        paragrafo.setAlignment(alinhamento);
        paragrafo.setSpacingAfter(documentoLayoutConfig.espacamentoPadraoDepoisParagrafoTwips());

        return paragrafo;
    }

    private void adicionarTextoNegrito(XWPFParagraph paragrafo, String texto) {
        XWPFRun run = paragrafo.createRun();

        aplicarFontePadrao(run);

        run.setBold(true);
        run.setText(texto);
    }

    private void adicionarTextoNormal(XWPFParagraph paragrafo, String texto) {
        XWPFRun run = paragrafo.createRun();

        aplicarFontePadrao(run);
        run.setText(texto);
    }

    private void aplicarFontePadrao(XWPFRun run) {
        run.setFontFamily(documentoLayoutConfig.fonteTexto());
        run.setFontSize(documentoLayoutConfig.tamanhoFonteTexto());
    }

    private void adicionarTextoComQuebras(XWPFRun run, String texto) {
        String[] linhas = texto.split("\\R", -1);

        for (int i = 0; i < linhas.length; i++) {
            if (i > 0) {
                run.addBreak();
            }

            run.setText(linhas[i]);
        }
    }

    private void criarEspaco(XWPFDocument documento, int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            XWPFParagraph paragrafo = documento.createParagraph();

            paragrafo.setSpacingBefore(0);
            paragrafo.setSpacingAfter(0);

            XWPFRun run = paragrafo.createRun();

            run.addBreak();
        }
    }

    private TipoPapelDocumento normalizarTipoPapel(String tipoPapel) {
        if (tipoPapel == null || tipoPapel.isBlank()) {
            return TipoPapelDocumento.A4;
        }

        try {
            return TipoPapelDocumento.valueOf(tipoPapel.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de papel inválido. Use A4 ou CARTA.");
        }
    }

    private String montarDataAcompanhamento(RelatorioPsicologico relatorio) {
        String mes = formatarTextoDeCampo(relatorio.getMesAcompanhamento());
        String ano = valor(relatorio.getAnoAcompanhamento());

        if (mes.isBlank() && ano.isBlank()) {
            return "";
        }

        if (mes.isBlank()) {
            return ano;
        }

        if (ano.isBlank()) {
            return mes;
        }

        return mes + " de " + ano;
    }

    private String montarTextoOcorrencia(Integer ocorrencia) {
        if (ocorrencia == null || ocorrencia <= 0) {
            return "";
        }

        if (ocorrencia == 1) {
            return "1 vez";
        }

        return ocorrencia + " vezes";
    }

    private String formatarTextoDeCampo(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }

        return texto
                .trim()
                .replace("-", " ")
                .replace("_", " ")
                .replaceAll("\s+", " ");
    }

    private String montarNomeArquivo(Paciente paciente, RelatorioPsicologico relatorio) {
        String nomePaciente = "paciente";

        if (paciente != null && paciente.getNomeCompleto() != null && !paciente.getNomeCompleto().isBlank()) {
            nomePaciente = paciente.getNomeCompleto();
        }

        String data = "sem-data";

        if (relatorio.getDataCriacao() != null) {
            data = relatorio.getDataCriacao().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        }

        return "Relatorio-Psicologico-" + data + "-" + normalizarNomeArquivo(nomePaciente) + ".docx";
    }

    private String normalizarNomeArquivo(String valor) {
        return valor
                .trim()
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("\\s+", "_");
    }

    private String valor(String valor) {
        if (valor == null) {
            return "";
        }

        return valor;
    }

    private String valor(Integer valor) {
        if (valor == null) {
            return "";
        }

        return String.valueOf(valor);
    }
}