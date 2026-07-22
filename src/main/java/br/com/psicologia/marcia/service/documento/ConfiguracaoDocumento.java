package br.com.psicologia.marcia.service.documento;

import java.math.BigInteger;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.model.enums.TipoPapelDocumento;

@Service
public class ConfiguracaoDocumento {

    private final DocumentoLayoutConfig documentoLayoutConfig;

    public ConfiguracaoDocumento(DocumentoLayoutConfig documentoLayoutConfig) {
        this.documentoLayoutConfig = documentoLayoutConfig;
    }

    public void configurar(XWPFDocument documento, TipoPapelDocumento tipoPapel) {
        TipoPapelDocumento tipoPapelNormalizado = documentoLayoutConfig.normalizarTipoPapel(tipoPapel);

        configurarPagina(
                documento,
                tipoPapelNormalizado,
                documentoLayoutConfig.larguraPaginaTwips(tipoPapelNormalizado),
                documentoLayoutConfig.alturaPaginaTwips(tipoPapelNormalizado)
        );
    }

    public void configurarA4(XWPFDocument documento) {
        configurar(documento, TipoPapelDocumento.A4);
    }

    public void configurarCarta(XWPFDocument documento) {
        configurar(documento, TipoPapelDocumento.CARTA);
    }

    public int obterLinhasPorPagina(TipoPapelDocumento tipoPapel) {
        return documentoLayoutConfig.linhasPorPagina(tipoPapel);
    }

    public TipoPapelDocumento normalizarTipoPapel(TipoPapelDocumento tipoPapel) {
        return documentoLayoutConfig.normalizarTipoPapel(tipoPapel);
    }

    private void configurarPagina(
            XWPFDocument documento,
            TipoPapelDocumento tipoPapel,
            int larguraTwips,
            int alturaTwips
    ) {
        TipoPapelDocumento tipoPapelNormalizado = documentoLayoutConfig.normalizarTipoPapel(tipoPapel);

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

        var pageMar = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();

        pageMar.setTop(BigInteger.valueOf(documentoLayoutConfig.margemSuperiorTwips()));
        pageMar.setBottom(BigInteger.valueOf(documentoLayoutConfig.margemInferiorTwips()));
        pageMar.setLeft(BigInteger.valueOf(documentoLayoutConfig.margemEsquerdaTwips()));
        pageMar.setRight(BigInteger.valueOf(documentoLayoutConfig.margemDireitaTwips()));

        pageMar.setHeader(BigInteger.valueOf(
                documentoLayoutConfig.margemCabecalhoTwips(tipoPapelNormalizado)
        ));

        pageMar.setFooter(BigInteger.valueOf(documentoLayoutConfig.margemRodapeTwips()));
    }
}