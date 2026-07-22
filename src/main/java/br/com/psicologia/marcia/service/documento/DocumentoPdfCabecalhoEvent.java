package br.com.psicologia.marcia.service.documento;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import br.com.psicologia.marcia.model.Psicologo;

public class DocumentoPdfCabecalhoEvent extends PdfPageEventHelper {

    private final DocumentoLayoutConfig documentoLayoutConfig;
    private final Psicologo psicologo;

    public DocumentoPdfCabecalhoEvent(
            DocumentoLayoutConfig documentoLayoutConfig,
            Psicologo psicologo
    ) {
        this.documentoLayoutConfig = documentoLayoutConfig;
        this.psicologo = psicologo;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            PdfPTable tabela = criarTabelaCabecalho();

            Rectangle pagina = document.getPageSize();

            float x = ((pagina.getWidth() - documentoLayoutConfig.larguraTabelaCabecalhoPdf()) / 2)
                    + documentoLayoutConfig.ajusteHorizontalCabecalhoPdf();

            float y = documentoLayoutConfig.posicaoTopoCabecalhoPdf();

            tabela.writeSelectedRows(0, -1, x, y, writer.getDirectContent());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desenhar cabeçalho fixo do PDF.", e);
        }
    }

    private PdfPTable criarTabelaCabecalho() throws DocumentException {
        PdfPTable tabela = new PdfPTable(3);

        tabela.setTotalWidth(documentoLayoutConfig.larguraTabelaCabecalhoPdf());
        tabela.setLockedWidth(true);
        tabela.setWidths(new float[]{
                documentoLayoutConfig.larguraCelulaLogoCabecalhoPdf(),
                documentoLayoutConfig.larguraCelulaTextoCabecalhoPdf(),
                documentoLayoutConfig.larguraCelulaDetalheCabecalhoPdf()
        });

        PdfPCell celulaLogo = criarCelulaSemBorda();
        PdfPCell celulaTexto = criarCelulaSemBorda();
        PdfPCell celulaDetalhe = criarCelulaSemBorda();
        celulaDetalhe.setPaddingRight(-10f); // Ajuste da imagem direita - margin quanto ^ mais pertp

        Image logo = carregarImagem(
                documentoLayoutConfig.caminhoLogoCabecalho(),
                documentoLayoutConfig.logoLarguraCm(),
                documentoLayoutConfig.logoAlturaCm()
        );

        if (logo != null) {
            logo.setAlignment(Image.ALIGN_LEFT);
            celulaLogo.addElement(logo);
        }

        adicionarTextoCabecalho(celulaTexto);

        Image detalhe = carregarImagem(
                documentoLayoutConfig.caminhoDetalheCabecalho(),
                documentoLayoutConfig.detalheLarguraCm(),
                documentoLayoutConfig.detalheAlturaCm()
        );

        if (detalhe != null) {
            detalhe.setAlignment(Image.ALIGN_RIGHT);
            celulaDetalhe.addElement(detalhe);
        }

        tabela.addCell(celulaLogo);
        tabela.addCell(celulaTexto);
        tabela.addCell(celulaDetalhe);

        return tabela;
    }

    private PdfPCell criarCelulaSemBorda() {
        PdfPCell celula = new PdfPCell();

        celula.setBorder(Rectangle.NO_BORDER);
        celula.setPadding(0);
        celula.setVerticalAlignment(Element.ALIGN_MIDDLE);

        return celula;
    }

    private Image carregarImagem(String caminhoImagem, double larguraCm, double alturaCm) {
        ClassPathResource imagemResource = new ClassPathResource(caminhoImagem);

        if (!imagemResource.exists()) {
            return null;
        }

        try (InputStream inputStream = imagemResource.getInputStream()) {
            byte[] bytesImagem = inputStream.readAllBytes();

            Image imagem = Image.getInstance(bytesImagem);

            imagem.scaleAbsolute(
                    documentoLayoutConfig.centimetrosParaPontos(larguraCm),
                    documentoLayoutConfig.centimetrosParaPontos(alturaCm)
            );

            return imagem;
        } catch (IOException | DocumentException e) {
            throw new RuntimeException("Erro ao carregar imagem do cabeçalho do PDF: " + caminhoImagem, e);
        }
    }

    private void adicionarTextoCabecalho(PdfPCell celulaTexto) {
        Paragraph nome = new Paragraph(valor(psicologo.getNome()), fonteCabecalho());

        nome.setAlignment(Element.ALIGN_CENTER);
        nome.setSpacingAfter(2);

        Paragraph funcao = new Paragraph(
                valor(psicologo.getFuncaoEmpresa()) + " CRP:" + valor(psicologo.getCrp()),
                fonteCabecalho()
        );

        funcao.setAlignment(Element.ALIGN_CENTER);
        funcao.setSpacingAfter(2);

        Paragraph contato = new Paragraph(
                "Tel. " + valor(psicologo.getTelefone()) + " e-mail: " + valor(psicologo.getEmail()),
                fonteCabecalho()
        );

        contato.setAlignment(Element.ALIGN_CENTER);
        contato.setSpacingAfter(0);

        celulaTexto.addElement(nome);
        celulaTexto.addElement(funcao);
        celulaTexto.addElement(contato);
    }

    private Font fonteCabecalho() {
        return FontFactory.getFont(
                documentoLayoutConfig.fonteCabecalho(),
                documentoLayoutConfig.tamanhoFonteCabecalho(),
                Font.ITALIC
        );
    }

    private String valor(String valor) {
        if (valor == null) {
            return "";
        }

        return valor;
    }
}