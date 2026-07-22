package br.com.psicologia.marcia.service.documento;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.model.Psicologo;
import br.com.psicologia.marcia.model.enums.TipoPapelDocumento;

@Service
public class Cabecalho {

    private final DocumentoLayoutConfig documentoLayoutConfig;

    public Cabecalho(DocumentoLayoutConfig documentoLayoutConfig) {
        this.documentoLayoutConfig = documentoLayoutConfig;
    }

    public void criar(XWPFDocument documento, Psicologo psicologo, TipoPapelDocumento tipoPapel) {
        TipoPapelDocumento tipoPapelNormalizado = documentoLayoutConfig.normalizarTipoPapel(tipoPapel);

        XWPFHeaderFooterPolicy policy = documento.getHeaderFooterPolicy();

        if (policy == null) {
            policy = new XWPFHeaderFooterPolicy(documento);
        }

        XWPFHeader header = policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
        XWPFTable tabela = header.createTable(1, 4);

        configurarTabelaCabecalho(tabela, tipoPapelNormalizado);

        XWPFTableCell celulaLogo = tabela.getRow(0).getCell(0);
        XWPFTableCell celulaTexto = tabela.getRow(0).getCell(1);
        XWPFTableCell celulaDetalhe = tabela.getRow(0).getCell(2);
        XWPFTableCell celulaEspacoDireita = tabela.getRow(0).getCell(3);

        configurarCelulaCabecalho(celulaLogo);
        configurarCelulaCabecalho(celulaTexto);
        configurarCelulaCabecalho(celulaDetalhe);
        configurarCelulaCabecalho(celulaEspacoDireita);

        definirLarguraCelula(celulaLogo, documentoLayoutConfig.larguraCelulaLogoCabecalhoTwips());
        definirLarguraCelula(celulaTexto, documentoLayoutConfig.larguraCelulaTextoCabecalhoTwips(tipoPapelNormalizado));
        definirLarguraCelula(celulaDetalhe, documentoLayoutConfig.larguraCelulaDetalheCabecalhoTwips());
        definirLarguraCelula(celulaEspacoDireita, documentoLayoutConfig.larguraCelulaEspacoDireitaCabecalhoTwips(tipoPapelNormalizado));

        criarImagemCabecalho(
                celulaLogo,
                documentoLayoutConfig.caminhoLogoCabecalho(),
                ParagraphAlignment.LEFT,
                documentoLayoutConfig.logoLarguraCm(),
                documentoLayoutConfig.logoAlturaCm()
        );

        criarTextoCabecalho(celulaTexto, psicologo);

        criarImagemCabecalho(
                celulaDetalhe,
                documentoLayoutConfig.caminhoDetalheCabecalho(),
                ParagraphAlignment.RIGHT,
                documentoLayoutConfig.detalheLarguraCm(),
                documentoLayoutConfig.detalheAlturaCm()
        );
    }

    private void configurarTabelaCabecalho(XWPFTable tabela, TipoPapelDocumento tipoPapel) {
        int margemSuperiorInterna = documentoLayoutConfig.margemSuperiorInternaCabecalhoTwips(tipoPapel);

        tabela.setCellMargins(margemSuperiorInterna, 0, 0, 0);

        removerBordasTabela(tabela);

        CTTblPr tblPr = tabela.getCTTbl().getTblPr();

        if (tblPr == null) {
            tblPr = tabela.getCTTbl().addNewTblPr();
        }

        CTTblWidth larguraTabela = tblPr.getTblW();

        if (larguraTabela == null) {
            larguraTabela = tblPr.addNewTblW();
        }

        larguraTabela.setType(STTblWidth.DXA);
        larguraTabela.setW(BigInteger.valueOf(documentoLayoutConfig.larguraTabelaCabecalhoTwips()));

        CTTblWidth indentacaoTabela = tblPr.getTblInd();

        if (indentacaoTabela == null) {
            indentacaoTabela = tblPr.addNewTblInd();
        }

        indentacaoTabela.setType(STTblWidth.DXA);
        indentacaoTabela.setW(BigInteger.valueOf(documentoLayoutConfig.indentacaoCabecalhoTwips(tipoPapel)));
    }

    private void configurarCelulaCabecalho(XWPFTableCell celula) {
        celula.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        while (!celula.getParagraphs().isEmpty()) {
            celula.removeParagraph(0);
        }
    }

    private void definirLarguraCelula(XWPFTableCell celula, int larguraTwips) {
        CTTcPr tcPr = celula.getCTTc().getTcPr();

        if (tcPr == null) {
            tcPr = celula.getCTTc().addNewTcPr();
        }

        CTTblWidth largura = tcPr.getTcW();

        if (largura == null) {
            largura = tcPr.addNewTcW();
        }

        largura.setW(BigInteger.valueOf(larguraTwips));
        largura.setType(STTblWidth.DXA);
    }

    private void criarImagemCabecalho(
            XWPFTableCell celula,
            String caminhoImagem,
            ParagraphAlignment alinhamento,
            double larguraCm,
            double alturaCm
    ) {
        XWPFParagraph paragrafo = celula.addParagraph();

        paragrafo.setAlignment(alinhamento);
        paragrafo.setSpacingBefore(0);
        paragrafo.setSpacingAfter(0);

        ClassPathResource imagem = new ClassPathResource(caminhoImagem);

        if (!imagem.exists()) {
            return;
        }

        try (InputStream inputStream = imagem.getInputStream()) {
            XWPFRun run = paragrafo.createRun();

            run.addPicture(
                    inputStream,
                    obterTipoImagem(caminhoImagem),
                    caminhoImagem,
                    documentoLayoutConfig.centimetrosParaEmu(larguraCm),
                    documentoLayoutConfig.centimetrosParaEmu(alturaCm)
            );
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException("Erro ao carregar imagem do cabeçalho: " + caminhoImagem, e);
        }
    }

    private int obterTipoImagem(String caminhoImagem) {
        String caminhoMinusculo = caminhoImagem.toLowerCase();

        if (caminhoMinusculo.endsWith(".png")) {
            return Document.PICTURE_TYPE_PNG;
        }

        if (
                caminhoMinusculo.endsWith(".jpg") ||
                        caminhoMinusculo.endsWith(".jpeg") ||
                        caminhoMinusculo.endsWith(".jfif")
        ) {
            return Document.PICTURE_TYPE_JPEG;
        }

        return Document.PICTURE_TYPE_PNG;
    }

    private void criarTextoCabecalho(XWPFTableCell celula, Psicologo psicologo) {
        XWPFParagraph paragrafoNome = celula.addParagraph();

        paragrafoNome.setAlignment(ParagraphAlignment.CENTER);
        paragrafoNome.setSpacingBefore(0);
        paragrafoNome.setSpacingAfter(20);

        XWPFRun nome = paragrafoNome.createRun();

        aplicarFonteCabecalho(nome);
        nome.setText(valor(psicologo.getNome()));

        XWPFParagraph paragrafoFuncao = celula.addParagraph();

        paragrafoFuncao.setAlignment(ParagraphAlignment.CENTER);
        paragrafoFuncao.setSpacingBefore(0);
        paragrafoFuncao.setSpacingAfter(20);

        XWPFRun funcao = paragrafoFuncao.createRun();

        aplicarFonteCabecalho(funcao);
        funcao.setText(valor(psicologo.getFuncaoEmpresa()) + " CRP:" + valor(psicologo.getCrp()));

        XWPFParagraph paragrafoContato = celula.addParagraph();

        paragrafoContato.setAlignment(ParagraphAlignment.CENTER);
        paragrafoContato.setSpacingBefore(0);
        paragrafoContato.setSpacingAfter(0);

        XWPFRun telefone = paragrafoContato.createRun();

        aplicarFonteCabecalho(telefone);
        telefone.setText("Tel. " + valor(psicologo.getTelefone()) + " e-mail: ");

        XWPFRun email = paragrafoContato.createRun();

        aplicarFonteCabecalho(email);
        email.setText(valor(psicologo.getEmail()));
        email.setColor("0000FF");
        email.setUnderline(UnderlinePatterns.SINGLE);
    }

    private void aplicarFonteCabecalho(XWPFRun run) {
        run.setFontFamily(documentoLayoutConfig.fonteCabecalho());
        run.setFontSize(documentoLayoutConfig.tamanhoFonteCabecalho());
        run.setItalic(true);
    }

    private void removerBordasTabela(XWPFTable tabela) {
        CTTblPr tblPr = tabela.getCTTbl().getTblPr();

        if (tblPr == null) {
            tblPr = tabela.getCTTbl().addNewTblPr();
        }

        CTTblBorders borders = tblPr.getTblBorders();

        if (borders == null) {
            borders = tblPr.addNewTblBorders();
        }

        aplicarBordaNula(borders.isSetTop() ? borders.getTop() : borders.addNewTop());
        aplicarBordaNula(borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom());
        aplicarBordaNula(borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft());
        aplicarBordaNula(borders.isSetRight() ? borders.getRight() : borders.addNewRight());
        aplicarBordaNula(borders.isSetInsideH() ? borders.getInsideH() : borders.addNewInsideH());
        aplicarBordaNula(borders.isSetInsideV() ? borders.getInsideV() : borders.addNewInsideV());
    }

    private void aplicarBordaNula(CTBorder border) {
        border.setVal(STBorder.NIL);
        border.setSz(BigInteger.ZERO);
        border.setSpace(BigInteger.ZERO);
        border.setColor("FFFFFF");
    }

    private String valor(String valor) {
        if (valor == null) {
            return "";
        }

        return valor;
    }
}