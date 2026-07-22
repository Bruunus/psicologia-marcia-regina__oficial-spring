package br.com.psicologia.marcia.service.documento;

import org.springframework.stereotype.Component;

import br.com.psicologia.marcia.model.enums.TipoPapelDocumento;

@Component
public class DocumentoLayoutConfig {

    public String fonteTexto() {
        return "Arial";
    }

    public String fonteTextoPdf() {
        return "Arial";
    }

    public String fonteCabecalho() {
        return "Times New Roman";
    }

    public int tamanhoFonteTexto() {
        return 10;
    }

    public int tamanhoFonteCabecalho() {
        return 11;
    }

    public int tamanhoFonteTitulo() {
        return 16;
    }

    public String caminhoLogoCabecalho() {
        return "documentos/imagens/logo-documento.jfif";
    }

    public String caminhoDetalheCabecalho() {
        return "documentos/imagens/ilustrador-de-documento.png";
    }

    public double logoLarguraCm() {
        return 2.73;
    }

    public double logoAlturaCm() {
        return 2.67;
    }

    public double detalheLarguraCm() {
        return 3.78;
    }

    public double detalheAlturaCm() {
        return 2.87;
    }

    public double detalheCabecalhoPdfLarguraCm() {
        return 3.78;
    }

    public double detalheCabecalhoPdfAlturaCm() {
        return 2.87;
    }

    public int larguraPaginaTwips(TipoPapelDocumento tipoPapel) {
        if (normalizarTipoPapel(tipoPapel) == TipoPapelDocumento.CARTA) {
            return 12240;
        }

        return 11906;
    }

    public int alturaPaginaTwips(TipoPapelDocumento tipoPapel) {
        if (normalizarTipoPapel(tipoPapel) == TipoPapelDocumento.CARTA) {
            return 15840;
        }

        return 16838;
    }

    public int margemSuperiorTwips() {
        return 1900;
    }

    public int margemInferiorTwips() {
        return 900;
    }

    public int margemEsquerdaTwips() {
        return 1440;
    }

    public int margemDireitaTwips() {
        return 1440;
    }

    public int margemCabecalhoTwips() {
        return 250;
    }

    public int margemCabecalhoTwips(TipoPapelDocumento tipoPapel) {
        return 250;
    }

    public int margemSuperiorInternaCabecalhoTwips(TipoPapelDocumento tipoPapel) {
        if (normalizarTipoPapel(tipoPapel) == TipoPapelDocumento.CARTA) {
            return 280;
        }

        return 280;
    }

    public int margemRodapeTwips() {
        return 450;
    }

    public float margemSuperiorPdf() {
        return 150f;
    }

    public float margemInferiorPdf() {
        return twipsParaPontos(margemInferiorTwips());
    }

    public float margemEsquerdaPdf() {
        return twipsParaPontos(margemEsquerdaTwips());
    }

    public float margemDireitaPdf() {
        return twipsParaPontos(margemDireitaTwips());
    }

    public int larguraTabelaCabecalhoTwips() {
        return 11520;
    }

    public int larguraCelulaLogoCabecalhoTwips() {
        return 2500;
    }

    public int larguraCelulaTextoCabecalhoTwips() {
        return 6500;
    }

    public int larguraCelulaTextoCabecalhoTwips(TipoPapelDocumento tipoPapel) {
        return larguraCelulaTextoCabecalhoTwips() - larguraCelulaEspacoDireitaCabecalhoTwips(tipoPapel);
    }

    public int larguraCelulaDetalheCabecalhoTwips() {
        return 2520;
    }

    public int larguraCelulaEspacoDireitaCabecalhoTwips(TipoPapelDocumento tipoPapel) {
        if (normalizarTipoPapel(tipoPapel) == TipoPapelDocumento.CARTA) {
            return 1;
        }

        return 450;
    }

    public int indentacaoCabecalhoTwips(TipoPapelDocumento tipoPapel) {
        if (normalizarTipoPapel(tipoPapel) == TipoPapelDocumento.CARTA) {
            return -885;
        }

        return -900;
    }

    public float larguraTabelaCabecalhoPdf() {
        return 535f;
    }

    public float larguraCelulaLogoCabecalhoPdf() {
        return 105f;
    }

    public float larguraCelulaTextoCabecalhoPdf() {
        return 310f;
    }

    public float larguraCelulaDetalheCabecalhoPdf() {
        return 105f;
    }

    public float posicaoTopoCabecalhoPdf() {
        return 815f;
    }

    public float ajusteHorizontalCabecalhoPdf() {
        return 0f;
    }

    public float ajusteDireitaImagemDetalheCabecalhoPdf() {
        return -15f;
    }

    public int espacamentoPadraoDepoisParagrafoTwips() {
        return 170;
    }

    public int espacamentoTituloAntesTwips() {
        return 260;
    }

    public int espacamentoTituloDepoisTwips() {
        return 420;
    }

    public int espacamentoCorpoAntesTwips() {
        return 120;
    }

    public int espacamentoCorpoDepoisTwips() {
        return 220;
    }

    public int recuoPrimeiraLinhaCorpoTwips() {
        return 720;
    }

    public int recuoHipoteseTwips() {
        return 720;
    }

    public int espacamentoAntesHipoteseTituloTwips() {
        return 260;
    }

    public int espacamentoDepoisHipoteseTituloTwips() {
        return 120;
    }

    public int espacamentoDepoisItemHipoteseTwips() {
        return 90;
    }

    public int espacamentoAntesFraseFinalTwips() {
        return 320;
    }

    public int espacamentoDepoisFraseFinalTwips() {
        return 220;
    }

    public int espacamentoAntesDataEmissaoTwips() {
        return 350;
    }

    public int espacamentoDepoisDataEmissaoTwips() {
        return 350;
    }

    public float espacamentoTituloAntesPdf() {
        return twipsParaPontos(espacamentoTituloAntesTwips());
    }

    public float espacamentoTituloDepoisPdf() {
        return twipsParaPontos(espacamentoTituloDepoisTwips());
    }

    public float espacamentoCorpoAntesPdf() {
        return twipsParaPontos(espacamentoCorpoAntesTwips());
    }

    public float espacamentoCorpoDepoisPdf() {
        return twipsParaPontos(espacamentoCorpoDepoisTwips());
    }

    public float recuoPrimeiraLinhaCorpoPdf() {
        return twipsParaPontos(recuoPrimeiraLinhaCorpoTwips());
    }

    public float recuoHipotesePdf() {
        return twipsParaPontos(recuoHipoteseTwips());
    }

    public float espacamentoAntesHipoteseTituloPdf() {
        return twipsParaPontos(espacamentoAntesHipoteseTituloTwips());
    }

    public float espacamentoDepoisHipoteseTituloPdf() {
        return twipsParaPontos(espacamentoDepoisHipoteseTituloTwips());
    }

    public float espacamentoDepoisItemHipotesePdf() {
        return twipsParaPontos(espacamentoDepoisItemHipoteseTwips());
    }

    public float espacamentoAntesFraseFinalPdf() {
        return twipsParaPontos(espacamentoAntesFraseFinalTwips());
    }

    public float espacamentoDepoisFraseFinalPdf() {
        return twipsParaPontos(espacamentoDepoisFraseFinalTwips());
    }

    public float espacamentoAntesDataEmissaoPdf() {
        return twipsParaPontos(espacamentoAntesDataEmissaoTwips());
    }

    public float espacamentoDepoisDataEmissaoPdf() {
        return twipsParaPontos(espacamentoDepoisDataEmissaoTwips());
    }

    public String linhaAssinatura() {
        return "________________________________________";
    }

    public int linhasAssinaturaDocx() {
        return 5;
    }

    public int linhasMinimasAntesAssinaturaDocx() {
        return 9;
    }

    public int quantidadeLinhasCriticasAssinaturaDocx(TipoPapelDocumento tipoPapel) {
        if (normalizarTipoPapel(tipoPapel) == TipoPapelDocumento.CARTA) {
            return 20;
        }

        return 999;
    }

    public int espacosPadraoAntesAssinaturaDocx(TipoPapelDocumento tipoPapel) {
        return 3;
    }

    public int espacosCriticosAntesAssinaturaDocx(TipoPapelDocumento tipoPapel) {
        if (normalizarTipoPapel(tipoPapel) == TipoPapelDocumento.CARTA) {
            return 46;
        }

        return espacosPadraoAntesAssinaturaDocx(tipoPapel);
    }

    public int quantidadeEspacosAntesAssinaturaDocx(
            TipoPapelDocumento tipoPapel,
            int quantidadeLinhasConteudo
    ) {
        if (
                normalizarTipoPapel(tipoPapel) == TipoPapelDocumento.CARTA &&
                        quantidadeLinhasConteudo >= quantidadeLinhasCriticasAssinaturaDocx(tipoPapel)
        ) {
            return espacosCriticosAntesAssinaturaDocx(tipoPapel);
        }

        return espacosPadraoAntesAssinaturaDocx(tipoPapel);
    }

    public int caracteresPorLinhaConteudoDocx(TipoPapelDocumento tipoPapel) {
        if (normalizarTipoPapel(tipoPapel) == TipoPapelDocumento.CARTA) {
            return 92;
        }

        return 95;
    }

    public float posicaoMinimaAssinaturaPdf() {
        return 250f;
    }

    public float posicaoTopoAssinaturaPdf() {
        return 245f;
    }

    public int espacamentoDepoisLinhaAssinaturaTwips() {
        return 80;
    }

    public int espacamentoDepoisNomeAssinaturaTwips() {
        return 80;
    }

    public int espacamentoDepoisFuncaoAssinaturaTwips() {
        return 80;
    }

    public float espacamentoDepoisLinhaAssinaturaPdf() {
        return twipsParaPontos(espacamentoDepoisLinhaAssinaturaTwips());
    }

    public float espacamentoDepoisNomeAssinaturaPdf() {
        return twipsParaPontos(espacamentoDepoisNomeAssinaturaTwips());
    }

    public float espacamentoDepoisFuncaoAssinaturaPdf() {
        return twipsParaPontos(espacamentoDepoisFuncaoAssinaturaTwips());
    }

    public int linhasPorPagina(TipoPapelDocumento tipoPapel) {
        if (normalizarTipoPapel(tipoPapel) == TipoPapelDocumento.CARTA) {
            return 39;
        }

        return 42;
    }

    public float twipsParaPontos(int twips) {
        return twips / 20f;
    }

    public int pontosParaTwips(float pontos) {
        return Math.round(pontos * 20f);
    }

    public float centimetrosParaPontos(double centimetros) {
        return (float) (centimetros * 28.3464567);
    }

    public int centimetrosParaEmu(double centimetros) {
        return (int) Math.round(centimetros * 360000);
    }

    public TipoPapelDocumento normalizarTipoPapel(TipoPapelDocumento tipoPapel) {
        if (tipoPapel == null) {
            return TipoPapelDocumento.A4;
        }

        return tipoPapel;
    }
}