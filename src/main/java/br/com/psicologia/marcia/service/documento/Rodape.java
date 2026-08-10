package br.com.psicologia.marcia.service.documento;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.model.Psicologo;
import br.com.psicologia.marcia.service.psicologo.PsicologoService;

@Service
public class Rodape {

    private final DocumentoLayoutConfig documentoLayoutConfig;
    private final PsicologoService psicologoService;

    public Rodape(
        DocumentoLayoutConfig documentoLayoutConfig,
        PsicologoService psicologoService
    ) {
        this.documentoLayoutConfig = documentoLayoutConfig;
        this.psicologoService = psicologoService;
    }

    /*
     * Mantém compatibilidade com documentos antigos que chamam
     * apenas rodape.criar(documento).
     */
    public void criar(XWPFDocument documento) {
        Psicologo psicologo =
            psicologoService.buscarPsicologoAtivoPrincipal();

        criar(documento, psicologo);
    }

    /*
     * Usado quando o serviço de geração já buscou o psicólogo.
     */
    public void criar(
        XWPFDocument documento,
        Psicologo psicologo
    ) {
        if (psicologo == null) {
            throw new RuntimeException(
                "Psicólogo ativo não encontrado para criação do rodapé."
            );
        }

        criarRodape(
            documento,
            psicologo.getTelefone(),
            psicologo.getEmail()
        );
    }

    private void criarRodape(
        XWPFDocument documento,
        String telefone,
        String email
    ) {
        XWPFHeaderFooterPolicy policy =
            documento.getHeaderFooterPolicy();

        if (policy == null) {
            policy = new XWPFHeaderFooterPolicy(documento);
        }

        XWPFFooter footer = policy.createFooter(
            XWPFHeaderFooterPolicy.DEFAULT
        );

        XWPFTable tabela = footer.createTable(1, 3);

        configurarTabela(tabela);

        XWPFTableCell celulaTelefone =
            tabela.getRow(0).getCell(0);

        XWPFTableCell celulaLogo =
            tabela.getRow(0).getCell(1);

        XWPFTableCell celulaEmail =
            tabela.getRow(0).getCell(2);

        configurarCelula(celulaTelefone);
        configurarCelula(celulaLogo);
        configurarCelula(celulaEmail);

        definirLarguraCelula(
            celulaTelefone,
            documentoLayoutConfig
                .larguraCelulaTelefoneRodapeTwips()
        );

        definirLarguraCelula(
            celulaLogo,
            documentoLayoutConfig
                .larguraCelulaLogoRodapeTwips()
        );

        definirLarguraCelula(
            celulaEmail,
            documentoLayoutConfig
                .larguraCelulaEmailRodapeTwips()
        );

        impedirQuebraLinhaCelula(celulaTelefone);
        impedirQuebraLinhaCelula(celulaEmail);

        criarTexto(
            celulaTelefone,
            "Telefone: " + valor(telefone),
            ParagraphAlignment.LEFT
        );

        criarImagemLogo(celulaLogo);

        criarTexto(
            celulaEmail,
            valor(email),
            ParagraphAlignment.RIGHT
        );
    }

    private void configurarTabela(
        XWPFTable tabela
    ) {
        tabela.setCellMargins(
            0,
            0,
            0,
            0
        );

        tabela.setTableAlignment(
            TableRowAlign.CENTER
        );

        CTTblPr tblPr =
            tabela.getCTTbl().getTblPr();

        if (tblPr == null) {
            tblPr =
                tabela.getCTTbl().addNewTblPr();
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
                documentoLayoutConfig
                    .larguraTabelaRodapeTwips()
            )
        );

        removerBordasTabela(tabela);
    }

    private void configurarCelula(
        XWPFTableCell celula
    ) {
        celula.setVerticalAlignment(
            XWPFTableCell.XWPFVertAlign.CENTER
        );

        zerarMargensInternasCelula(celula);

        while (!celula.getParagraphs().isEmpty()) {
            celula.removeParagraph(0);
        }
    }

    private void zerarMargensInternasCelula(
        XWPFTableCell celula
    ) {
        CTTcPr tcPr =
            obterPropriedadesCelula(celula);

        CTTcMar margens =
            tcPr.isSetTcMar()
                ? tcPr.getTcMar()
                : tcPr.addNewTcMar();

        if (!margens.isSetTop()) {
            margens.addNewTop();
        }

        if (!margens.isSetBottom()) {
            margens.addNewBottom();
        }

        if (!margens.isSetLeft()) {
            margens.addNewLeft();
        }

        if (!margens.isSetRight()) {
            margens.addNewRight();
        }

        margens.getTop().setW(BigInteger.ZERO);
        margens.getBottom().setW(BigInteger.ZERO);
        margens.getLeft().setW(BigInteger.ZERO);
        margens.getRight().setW(BigInteger.ZERO);
    }

    private void definirLarguraCelula(
        XWPFTableCell celula,
        int larguraTwips
    ) {
        CTTcPr tcPr =
            obterPropriedadesCelula(celula);

        CTTblWidth largura =
            tcPr.getTcW();

        if (largura == null) {
            largura = tcPr.addNewTcW();
        }

        largura.setType(
            STTblWidth.DXA
        );

        largura.setW(
            BigInteger.valueOf(larguraTwips)
        );
    }

    private void impedirQuebraLinhaCelula(
        XWPFTableCell celula
    ) {
        CTTcPr tcPr =
            obterPropriedadesCelula(celula);

        if (!tcPr.isSetNoWrap()) {
            tcPr.addNewNoWrap();
        }
    }

    private CTTcPr obterPropriedadesCelula(
        XWPFTableCell celula
    ) {
        CTTcPr tcPr =
            celula.getCTTc().getTcPr();

        if (tcPr == null) {
            tcPr =
                celula.getCTTc().addNewTcPr();
        }

        return tcPr;
    }

    private void criarTexto(
        XWPFTableCell celula,
        String texto,
        ParagraphAlignment alinhamento
    ) {
        XWPFParagraph paragrafo =
            celula.addParagraph();

        paragrafo.setAlignment(alinhamento);
        paragrafo.setIndentationLeft(0);
        paragrafo.setIndentationRight(0);
        paragrafo.setIndentationFirstLine(0);
        paragrafo.setSpacingBefore(0);
        paragrafo.setSpacingAfter(0);
        paragrafo.setWordWrapped(false);

        XWPFRun run =
            paragrafo.createRun();

        run.setFontFamily(
            documentoLayoutConfig
                .fonteRodape()
        );

        run.setFontSize(
            documentoLayoutConfig
                .tamanhoFonteRodape()
        );

        run.setItalic(
            documentoLayoutConfig
                .rodapeItalico()
        );

        run.setText(valor(texto));
    }

    private void criarImagemLogo(
        XWPFTableCell celula
    ) {
        XWPFParagraph paragrafo =
            celula.addParagraph();

        paragrafo.setAlignment(
            ParagraphAlignment.CENTER
        );

        /*
         * Ajuste negativo desloca visualmente o logo para a esquerda.
         * O valor permanece centralizado no DocumentoLayoutConfig.
         */
        paragrafo.setIndentationLeft(
            documentoLayoutConfig
                .ajusteHorizontalLogoRodapeTwips()
        );

        paragrafo.setIndentationRight(0);
        paragrafo.setIndentationFirstLine(0);
        paragrafo.setSpacingBefore(0);
        paragrafo.setSpacingAfter(0);
        paragrafo.setWordWrapped(false);

        String caminhoImagem =
            documentoLayoutConfig
                .caminhoLogoCabecalho();

        ClassPathResource imagem =
            new ClassPathResource(caminhoImagem);

        if (!imagem.exists()) {
            return;
        }

        try (
            InputStream inputStream =
                imagem.getInputStream()
        ) {
            XWPFRun run =
                paragrafo.createRun();

            run.addPicture(
                inputStream,
                obterTipoImagem(caminhoImagem),
                caminhoImagem,
                documentoLayoutConfig
                    .centimetrosParaEmu(0.75),
                documentoLayoutConfig
                    .centimetrosParaEmu(0.75)
            );
        } catch (
            IOException |
            InvalidFormatException e
        ) {
            throw new RuntimeException(
                "Erro ao carregar o logotipo do rodapé.",
                e
            );
        }
    }

    private int obterTipoImagem(
        String caminhoImagem
    ) {
        String caminhoMinusculo =
            caminhoImagem.toLowerCase();

        if (caminhoMinusculo.endsWith(".png")) {
            return Document.PICTURE_TYPE_PNG;
        }

        if (
            caminhoMinusculo.endsWith(".jpg")
                || caminhoMinusculo.endsWith(".jpeg")
                || caminhoMinusculo.endsWith(".jfif")
        ) {
            return Document.PICTURE_TYPE_JPEG;
        }

        return Document.PICTURE_TYPE_PNG;
    }

    private void removerBordasTabela(
        XWPFTable tabela
    ) {
        CTTblPr tblPr =
            tabela.getCTTbl().getTblPr();

        if (tblPr == null) {
            tblPr =
                tabela.getCTTbl().addNewTblPr();
        }

        CTTblBorders borders =
            tblPr.getTblBorders();

        if (borders == null) {
            borders =
                tblPr.addNewTblBorders();
        }

        aplicarBordaNula(
            borders.isSetTop()
                ? borders.getTop()
                : borders.addNewTop()
        );

        aplicarBordaNula(
            borders.isSetBottom()
                ? borders.getBottom()
                : borders.addNewBottom()
        );

        aplicarBordaNula(
            borders.isSetLeft()
                ? borders.getLeft()
                : borders.addNewLeft()
        );

        aplicarBordaNula(
            borders.isSetRight()
                ? borders.getRight()
                : borders.addNewRight()
        );

        aplicarBordaNula(
            borders.isSetInsideH()
                ? borders.getInsideH()
                : borders.addNewInsideH()
        );

        aplicarBordaNula(
            borders.isSetInsideV()
                ? borders.getInsideV()
                : borders.addNewInsideV()
        );
    }
    
    

    private void aplicarBordaNula(
        CTBorder border
    ) {
        border.setVal(STBorder.NIL);
        border.setSz(BigInteger.ZERO);
        border.setSpace(BigInteger.ZERO);
        border.setColor("FFFFFF");
    }

    private String valor(
        String valor
    ) {
        return valor == null
            ? ""
            : valor;
    }
}