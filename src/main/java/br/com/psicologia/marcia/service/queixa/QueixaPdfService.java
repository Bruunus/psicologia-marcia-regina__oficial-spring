
package br.com.psicologia.marcia.service.queixa;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import br.com.psicologia.marcia.DTO.queixa.QueixaVisualizarRequest;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.Queixa;
import br.com.psicologia.marcia.repository.paciente.cadastro.QueixaRepository;
import br.com.psicologia.marcia.service.documento.DocumentoLayoutConfig;

@Service
public class QueixaPdfService {

    private static final String CAMINHO_LOGO =
            "documentos/imagens/logo-completo.png";

    private static final String ASSINATURA =
            "MARCIA REGINA - PSICÓLOGA E NEUROPSICÓLOGA";

    private static final int TAMANHO_FONTE_PADRAO = 11;
    private static final int TAMANHO_FONTE_TITULO = 14;

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATO_DATA_EXTENSO =
            DateTimeFormatter.ofPattern(
                    "d 'de' MMMM 'de' yyyy",
                    Locale.forLanguageTag("pt-BR")
            );

    private final QueixaRepository queixaRepository;
    private final DocumentoLayoutConfig layoutConfig;

    public QueixaPdfService(
            QueixaRepository queixaRepository,
            DocumentoLayoutConfig layoutConfig
    ) {
        this.queixaRepository = queixaRepository;
        this.layoutConfig = layoutConfig;
    }

    @Transactional(readOnly = true)
    public byte[] gerarPdf(QueixaVisualizarRequest request) {

        if (request == null || request.queixaId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ID da queixa é obrigatório."
            );
        }

        Queixa queixa = queixaRepository
                .findById(request.queixaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Queixa não encontrada."
                ));

        Paciente paciente = queixa.getPaciente();

        if (paciente == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Paciente vinculado à queixa não encontrado."
            );
        }

        try (ByteArrayOutputStream saida = new ByteArrayOutputStream()) {

            // Mesmas margens utilizadas no documento WORD.
            Document documento = new Document(
                    PageSize.A4,
                    layoutConfig.margemEsquerdaTwips() / 20f,
                    layoutConfig.margemDireitaTwips() / 20f,
                    layoutConfig.margemSuperiorTwips() / 20f,
                    layoutConfig.margemInferiorTwips() / 20f
            );

            PdfWriter.getInstance(documento, saida);

            documento.open();

            try {
                adicionarLogotipo(documento);
                adicionarTitulo(documento);
                adicionarIdentificacao(documento, paciente);
                adicionarConteudo(documento, queixa.getQueixa());
                adicionarDataEmissao(documento, paciente);
                adicionarAssinatura(documento);
            } finally {
                documento.close();
            }

            return saida.toByteArray();

        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível gerar o PDF da queixa.",
                    exception
            );
        }
    }

    private void adicionarLogotipo(Document documento)
            throws Exception {

        ClassPathResource recurso =
                new ClassPathResource(CAMINHO_LOGO);

        if (!recurso.exists()) {
            throw new IllegalStateException(
                    "Logotipo não encontrado: " + CAMINHO_LOGO
            );
        }

        byte[] dadosImagem;

        try (InputStream entrada = recurso.getInputStream()) {
            dadosImagem = entrada.readAllBytes();
        }

        Image imagem = Image.getInstance(dadosImagem);

        // Mesma largura do logotipo WORD: 4 cm.
        float largura = 4.0f * 72f / 2.54f;

        float altura = largura
                * imagem.getHeight()
                / imagem.getWidth();

        imagem.scaleAbsolute(largura, altura);

        imagem.setAlignment(Element.ALIGN_CENTER);

        // 940 twips equivalem a 47 pontos.
        imagem.setSpacingAfter(940 / 20f);

        documento.add(imagem);
    }

    private void adicionarTitulo(Document documento)
            throws DocumentException {

        Paragraph titulo = new Paragraph(
                "Queixa",
                fonteTitulo()
        );

        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingBefore(0);

        // Mesmo espaçamento do título WORD.
        titulo.setSpacingAfter(700 / 20f);

        documento.add(titulo);
    }

    private void adicionarIdentificacao(
            Document documento,
            Paciente paciente
    ) throws DocumentException {

        adicionarLinhaIdentificacao(
                documento,
                "Paciente: ",
                paciente.getNomeCompleto()
        );

        String dataCadastro =
                paciente.getDataCadastro() == null
                        ? "Não informada"
                        : paciente.getDataCadastro().format(
                                FORMATO_DATA
                        );

        adicionarLinhaIdentificacao(
                documento,
                "Data do registro: ",
                dataCadastro
        );
    }

    private void adicionarLinhaIdentificacao(
            Document documento,
            String rotulo,
            String valor
    ) throws DocumentException {

        Paragraph paragrafo = new Paragraph();

        paragrafo.setAlignment(Element.ALIGN_LEFT);
        paragrafo.setSpacingBefore(0);
        paragrafo.setSpacingAfter(120 / 20f);

        paragrafo.add(new Chunk(
                rotulo,
                fonteTextoNegrito()
        ));

        paragrafo.add(new Chunk(
                valor == null ? "" : valor,
                fonteTexto()
        ));

        documento.add(paragrafo);
    }

    private void adicionarConteudo(
            Document documento,
            String conteudo
    ) throws DocumentException {

        Paragraph titulo = new Paragraph(
                "Descrição da queixa",
                fonteTextoNegrito()
        );

        titulo.setAlignment(Element.ALIGN_LEFT);

        // Mesmos espaçamentos do subtítulo WORD.
        titulo.setSpacingBefore(560 / 20f);
        titulo.setSpacingAfter(250 / 20f);

        documento.add(titulo);

        String texto = conteudo == null ? "" : conteudo;
        String[] linhas = texto.split("\\R", -1);

        Paragraph paragrafo = new Paragraph();

        paragrafo.setAlignment(Element.ALIGN_JUSTIFIED);

        paragrafo.setFirstLineIndent(
                layoutConfig.recuoPrimeiraLinhaCorpoTwips() / 20f
        );

        paragrafo.setLeading(0f, 1.15f);
        paragrafo.setSpacingBefore(0);
        paragrafo.setSpacingAfter(200 / 20f);

        for (int i = 0; i < linhas.length; i++) {

            if (i > 0) {
                paragrafo.add(Chunk.NEWLINE);
            }

            paragrafo.add(new Chunk(
                    linhas[i],
                    fonteTexto()
            ));
        }

        documento.add(paragrafo);
    }

    private void adicionarDataEmissao(
            Document documento,
            Paciente paciente
    ) throws DocumentException {

        LocalDate data = paciente.getDataCadastro();

        String dataFormatada = data == null
                ? "Data não informada"
                : data.format(FORMATO_DATA_EXTENSO);

        Paragraph paragrafo = new Paragraph(
                "São Paulo, " + dataFormatada,
                fonteTexto()
        );

        paragrafo.setAlignment(Element.ALIGN_RIGHT);

        // Espaçamento equivalente aos 900 twips do WORD.
        paragrafo.setSpacingBefore(900 / 20f);
        paragrafo.setSpacingAfter(0);

        documento.add(paragrafo);
    }

    private void adicionarAssinatura(Document documento)
            throws DocumentException {

        Paragraph linha = new Paragraph(
                "_".repeat(46),
                fonteTexto()
        );

        linha.setAlignment(Element.ALIGN_CENTER);
        linha.setSpacingBefore(1350 / 20f);
        linha.setSpacingAfter(0);

        documento.add(linha);

        Paragraph nome = new Paragraph(
                ASSINATURA,
                fonteTexto()
        );

        nome.setAlignment(Element.ALIGN_CENTER);
        nome.setSpacingBefore(0);
        nome.setSpacingAfter(0);

        documento.add(nome);
    }

    private Font fonteTexto() {
        return FontFactory.getFont(
                layoutConfig.fonteTextoPdf(),
                TAMANHO_FONTE_PADRAO,
                Font.NORMAL
        );
    }

    private Font fonteTextoNegrito() {
        return FontFactory.getFont(
                layoutConfig.fonteTextoPdf(),
                TAMANHO_FONTE_PADRAO,
                Font.BOLD
        );
    }

    private Font fonteTitulo() {
        return FontFactory.getFont(
                layoutConfig.fonteTextoPdf(),
                TAMANHO_FONTE_TITULO,
                Font.BOLD
        );
    }
}
