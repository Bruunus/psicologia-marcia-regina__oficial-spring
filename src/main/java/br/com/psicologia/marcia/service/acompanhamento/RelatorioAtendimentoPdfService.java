
package br.com.psicologia.marcia.service.acompanhamento;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

import br.com.psicologia.marcia.DTO.acompanhamento.RelatorioAtendimentoVisualizarRequest;
import br.com.psicologia.marcia.model.AcompanhamentoPaciente;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.repository.acompanhamento.AcompanhamentoPacienteRepository;
import br.com.psicologia.marcia.service.documento.DocumentoLayoutConfig;

@Service
public class RelatorioAtendimentoPdfService {

    private static final String CAMINHO_LOGO =
            "documentos/imagens/logo-completo.png";

    private static final String ASSINATURA =
            "MARCIA REGINA - PSICÓLOGA E NEUROPSICÓLOGA";

    private static final int TAMANHO_FONTE_PADRAO = 11;
    private static final int TAMANHO_FONTE_TITULO = 14;

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private static final DateTimeFormatter FORMATO_DATA_EXTENSO =
            DateTimeFormatter.ofPattern(
                    "d 'de' MMMM 'de' yyyy",
                    Locale.forLanguageTag("pt-BR")
            );

    private final AcompanhamentoPacienteRepository repository;
    private final DocumentoLayoutConfig layoutConfig;

    public RelatorioAtendimentoPdfService(
            AcompanhamentoPacienteRepository repository,
            DocumentoLayoutConfig layoutConfig
    ) {
        this.repository = repository;
        this.layoutConfig = layoutConfig;
    }

    @Transactional(readOnly = true)
    public byte[] gerarPdf(
            RelatorioAtendimentoVisualizarRequest request
    ) {

        if (request == null || request.acompanhamentoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ID do acompanhamento é obrigatório."
            );
        }

        AcompanhamentoPaciente acompanhamento = repository
                .findByIdAndStatusDelete(
                        request.acompanhamentoId(),
                        StatusDelete.NAO_DELETADO
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Acompanhamento não encontrado."
                ));

        try (ByteArrayOutputStream saida = new ByteArrayOutputStream()) {

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
                adicionarIdentificacao(documento, acompanhamento);
                adicionarConteudo(documento, acompanhamento);
                adicionarDataEmissao(documento, acompanhamento);
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
                    "Não foi possível gerar o PDF do relatório de atendimento.",
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

        byte[] bytes;

        try (InputStream entrada = recurso.getInputStream()) {
            bytes = entrada.readAllBytes();
        }

        Image imagem = Image.getInstance(bytes);

        float largura = 4.0f * 72f / 2.54f;
        float altura = largura
                * imagem.getHeight()
                / imagem.getWidth();

        imagem.scaleAbsolute(largura, altura);
        imagem.setAlignment(Element.ALIGN_CENTER);
        imagem.setSpacingAfter(940 / 20f);

        documento.add(imagem);
    }

    private void adicionarTitulo(Document documento)
            throws DocumentException {

        Paragraph titulo = new Paragraph(
                "Relatório de Atendimento",
                fonteTitulo()
        );

        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingBefore(0);
        titulo.setSpacingAfter(700 / 20f);

        documento.add(titulo);
    }

    private void adicionarIdentificacao(
            Document documento,
            AcompanhamentoPaciente acompanhamento
    ) throws DocumentException {

        Paciente paciente = acompanhamento.getPaciente();

        String nomePaciente = paciente != null
                ? paciente.getNomeCompleto()
                : "Não informado";

        LocalDateTime dataHora =
                acompanhamento.getDataAcompanhamento();

        adicionarLinhaIdentificacao(
                documento,
                "Paciente: ",
                nomePaciente
        );

        adicionarLinhaIdentificacao(
                documento,
                "Data do atendimento: ",
                dataHora != null
                        ? dataHora.format(FORMATO_DATA)
                        : "Não informada"
        );

        adicionarLinhaIdentificacao(
                documento,
                "Horário: ",
                dataHora != null
                        ? dataHora.format(FORMATO_HORA)
                        : "Não informado"
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
            AcompanhamentoPaciente acompanhamento
    ) throws DocumentException {

        Paragraph titulo = new Paragraph(
                "Acompanhamento",
                fonteTextoNegrito()
        );

        titulo.setAlignment(Element.ALIGN_LEFT);
        titulo.setSpacingBefore(560 / 20f);
        titulo.setSpacingAfter(250 / 20f);

        documento.add(titulo);

        String texto = acompanhamento.getAcompanhamento() == null
                ? ""
                : acompanhamento.getAcompanhamento();

        String[] linhas = texto.split("\\R", -1);

        Paragraph paragrafo = new Paragraph();

        paragrafo.setAlignment(Element.ALIGN_JUSTIFIED);
        paragrafo.setFirstLineIndent(
                layoutConfig.recuoPrimeiraLinhaCorpoTwips() / 20f
        );
        paragrafo.setLeading(0, 1.15f);
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
            AcompanhamentoPaciente acompanhamento
    ) throws DocumentException {

        LocalDateTime dataHora =
                acompanhamento.getDataAcompanhamento();

        LocalDate data = dataHora != null
                ? dataHora.toLocalDate()
                : null;

        String dataFormatada = data == null
                ? "Data não informada"
                : data.format(FORMATO_DATA_EXTENSO);

        Paragraph paragrafo = new Paragraph(
                "São Paulo, " + dataFormatada,
                fonteTexto()
        );

        paragrafo.setAlignment(Element.ALIGN_RIGHT);
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
