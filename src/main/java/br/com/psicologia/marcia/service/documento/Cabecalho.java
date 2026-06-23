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

    private static final String FONTE_CABECALHO = "Times New Roman";

    private static final String CAMINHO_LOGO_CABECALHO = "documentos/imagens/logo-documento.jfif";
    private static final String CAMINHO_DETALHE_CABECALHO = "documentos/imagens/ilustrador-de-documento.png";

    private static final double LOGO_LARGURA_CM = 2.73;
    private static final double LOGO_ALTURA_CM = 2.67;

    private static final double DETALHE_LARGURA_CM = 3.78;
    private static final double DETALHE_ALTURA_CM = 2.87;

    private static final int LARGURA_TABELA_CABECALHO = 11520;

    private static final int LARGURA_CELULA_LOGO = 2500;
    private static final int LARGURA_CELULA_TEXTO = 6500;
    private static final int LARGURA_CELULA_DETALHE = 2520;

    /*
     * Ajuste fino da posição horizontal do cabeçalho por tipo de papel.
     *
     * Mais negativo  = joga o cabeçalho para a esquerda.
     * Menos negativo = joga o cabeçalho para a direita.
     */
    private static final int INDENTACAO_CABECALHO_A4 = -1080;  /* Se aumentar => direita | diminuir => esquerda */
    private static final int INDENTACAO_CABECALHO_CARTA = -885;

    public void criar(
            XWPFDocument documento,
            Psicologo psicologo,
            TipoPapelDocumento tipoPapel
    ) {
        XWPFHeaderFooterPolicy policy = documento.getHeaderFooterPolicy();

        if (policy == null) {
            policy = new XWPFHeaderFooterPolicy(documento);
        }

        XWPFHeader header = policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

        XWPFTable tabela = header.createTable(1, 3);
        configurarTabelaCabecalho(tabela, tipoPapel);

        XWPFTableCell celulaLogo = tabela.getRow(0).getCell(0);
        XWPFTableCell celulaTexto = tabela.getRow(0).getCell(1);
        XWPFTableCell celulaDetalhe = tabela.getRow(0).getCell(2);

        configurarCelulaCabecalho(celulaLogo);
        configurarCelulaCabecalho(celulaTexto);
        configurarCelulaCabecalho(celulaDetalhe);

        definirLarguraCelula(celulaLogo, LARGURA_CELULA_LOGO);
        definirLarguraCelula(celulaTexto, LARGURA_CELULA_TEXTO);
        definirLarguraCelula(celulaDetalhe, LARGURA_CELULA_DETALHE);

        criarImagemCabecalho(
                celulaLogo,
                CAMINHO_LOGO_CABECALHO,
                ParagraphAlignment.LEFT,
                LOGO_LARGURA_CM,
                LOGO_ALTURA_CM
        );

        criarTextoCabecalho(celulaTexto, psicologo);

        criarImagemCabecalho(
                celulaDetalhe,
                CAMINHO_DETALHE_CABECALHO,
                ParagraphAlignment.RIGHT,
                DETALHE_LARGURA_CM,
                DETALHE_ALTURA_CM
        );
    }

    private void configurarTabelaCabecalho(
            XWPFTable tabela,
            TipoPapelDocumento tipoPapel
    ) {
        tabela.setCellMargins(0, 0, 0, 0);

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
        larguraTabela.setW(BigInteger.valueOf(LARGURA_TABELA_CABECALHO));

        CTTblWidth indentacaoTabela = tblPr.getTblInd();

        if (indentacaoTabela == null) {
            indentacaoTabela = tblPr.addNewTblInd();
        }

        indentacaoTabela.setType(STTblWidth.DXA);
        indentacaoTabela.setW(BigInteger.valueOf(obterIndentacaoCabecalho(tipoPapel)));
    }

    private int obterIndentacaoCabecalho(TipoPapelDocumento tipoPapel) {
        if (tipoPapel == TipoPapelDocumento.CARTA) {
            return INDENTACAO_CABECALHO_CARTA;
        }

        return INDENTACAO_CABECALHO_A4;
    }

    private void configurarCelulaCabecalho(XWPFTableCell celula) {
        celula.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        while (!celula.getParagraphs().isEmpty()) {
            celula.removeParagraph(0);
        }
    }

    private void definirLarguraCelula(
            XWPFTableCell celula,
            int larguraTwips
    ) {
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
                    centimetrosParaEmu(larguraCm),
                    centimetrosParaEmu(alturaCm)
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

    private int centimetrosParaEmu(double centimetros) {
        return (int) Math.round(centimetros * 360000);
    }

    private void criarTextoCabecalho(
            XWPFTableCell celula,
            Psicologo psicologo
    ) {
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
        run.setFontFamily(FONTE_CABECALHO);
        run.setFontSize(11);
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