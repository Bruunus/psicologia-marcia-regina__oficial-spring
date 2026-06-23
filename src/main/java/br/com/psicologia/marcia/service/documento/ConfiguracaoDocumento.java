package br.com.psicologia.marcia.service.documento;

import java.math.BigInteger;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.model.enums.TipoPapelDocumento;

@Service
public class ConfiguracaoDocumento {

    private static final int A4_LARGURA_TWIPS = 11906;
    private static final int A4_ALTURA_TWIPS = 16838;

    private static final int CARTA_LARGURA_TWIPS = 12240;
    private static final int CARTA_ALTURA_TWIPS = 15840;

    private static final int LINHAS_POR_PAGINA_A4 = 42;
    private static final int LINHAS_POR_PAGINA_CARTA = 39;

    public void configurar(
            XWPFDocument documento,
            TipoPapelDocumento tipoPapel
    ) {
        TipoPapelDocumento tipoPapelNormalizado = normalizarTipoPapel(tipoPapel);

        if (tipoPapelNormalizado == TipoPapelDocumento.CARTA) {
            configurarCarta(documento);
            return;
        }

        configurarA4(documento);
    }

    public void configurarA4(XWPFDocument documento) {
        configurarPagina(
                documento,
                A4_LARGURA_TWIPS,
                A4_ALTURA_TWIPS
        );
    }

    public void configurarCarta(XWPFDocument documento) {
        configurarPagina(
                documento,
                CARTA_LARGURA_TWIPS,
                CARTA_ALTURA_TWIPS
        );
    }

    public int obterLinhasPorPagina(TipoPapelDocumento tipoPapel) {
        TipoPapelDocumento tipoPapelNormalizado = normalizarTipoPapel(tipoPapel);

        if (tipoPapelNormalizado == TipoPapelDocumento.CARTA) {
            return LINHAS_POR_PAGINA_CARTA;
        }

        return LINHAS_POR_PAGINA_A4;
    }

    public TipoPapelDocumento normalizarTipoPapel(TipoPapelDocumento tipoPapel) {
        if (tipoPapel == null) {
            return TipoPapelDocumento.A4;
        }

        return tipoPapel;
    }

    private void configurarPagina(
            XWPFDocument documento,
            int larguraTwips,
            int alturaTwips
    ) {
        if (documento.getDocument().getBody().getSectPr() == null) {
            documento.getDocument().getBody().addNewSectPr();
        }

        var sectPr = documento.getDocument().getBody().getSectPr();

        var pageSize = sectPr.getPgSz();

        if (pageSize == null) {
            pageSize = sectPr.addNewPgSz();
        }

        pageSize.setW(BigInteger.valueOf(larguraTwips));
        pageSize.setH(BigInteger.valueOf(alturaTwips));

        var pageMar = sectPr.isSetPgMar()
                ? sectPr.getPgMar()
                : sectPr.addNewPgMar();

        pageMar.setTop(BigInteger.valueOf(1900));
        pageMar.setBottom(BigInteger.valueOf(900));
        pageMar.setLeft(BigInteger.valueOf(1440));
        pageMar.setRight(BigInteger.valueOf(1440));
        pageMar.setHeader(BigInteger.valueOf(250));
        pageMar.setFooter(BigInteger.valueOf(450));
    }
}