
package br.com.psicologia.marcia.service.declaracao;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import br.com.psicologia.marcia.DTO.declaracao.DeclaracaoComparecimentoPdfRequest;
import br.com.psicologia.marcia.model.DeclaracaoComparecimento;
import br.com.psicologia.marcia.model.enums.OrientacaoDeclaracaoComparecimento;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.repository.declaracao.DeclaracaoComparecimentoRepository;
import br.com.psicologia.marcia.service.documento.DocumentoLayoutConfig;

@Service
public class DeclaracaoComparecimentoPdfService {

    private static final String CAMINHO_LOGO =
            "documentos/imagens/logo-completo.png";

    private static final double LOGO_LARGURA_CM = 4.10;
    private static final double LOGO_ALTURA_CM = 3.93;

    private final DeclaracaoComparecimentoRepository repository;
    private final DocumentoLayoutConfig layoutConfig;

    public DeclaracaoComparecimentoPdfService(
            DeclaracaoComparecimentoRepository repository,
            DocumentoLayoutConfig layoutConfig
    ) {
        this.repository = repository;
        this.layoutConfig = layoutConfig;
    }

    @Transactional(readOnly = true)
    public byte[] gerarPdf(DeclaracaoComparecimentoPdfRequest request) {

        if (request == null || request.declaracaoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ID da declaração é obrigatório."
            );
        }

        DeclaracaoComparecimento declaracao = repository
                .findByIdAndStatusDelete(
                        request.declaracaoId(),
                        StatusDelete.NAO_DELETADO
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Declaração não encontrada."
                ));

        try (ByteArrayOutputStream saida = new ByteArrayOutputStream()) {

            Document documento = new Document(
                    PageSize.A4,
                    layoutConfig.margemEsquerdaPdf(),
                    layoutConfig.margemDireitaPdf(),
                    35f,
                    layoutConfig.margemInferiorPdf()
            );

            PdfWriter.getInstance(documento, saida);

            documento.open();

            try {
                adicionarLogo(documento);
                adicionarEspaco(documento, 18f);
                adicionarTitulo(documento);
                adicionarEspaco(documento, 15f);
                adicionarTextoPrincipal(documento, declaracao);
                adicionarEspaco(documento, 15f);
                adicionarOrientacao(documento, declaracao);
                adicionarEspaco(documento, 12f);
                adicionarLinhasComentario(documento, 4);
                adicionarEspaco(documento, 12f);
                adicionarDataEmissao(documento, declaracao);
                adicionarEspaco(documento, 30f);
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
                    "Não foi possível gerar o PDF da declaração.",
                    exception
            );
        }
    }

    private void adicionarLogo(Document documento) throws Exception {

        ClassPathResource recurso = new ClassPathResource(CAMINHO_LOGO);

        if (!recurso.exists()) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Logotipo da declaração não encontrado."
            );
        }

        byte[] imagemBytes;

        try (InputStream entrada = recurso.getInputStream()) {
            imagemBytes = entrada.readAllBytes();
        }

        Image imagem = Image.getInstance(imagemBytes);

        imagem.scaleAbsolute(
                layoutConfig.centimetrosParaPontos(LOGO_LARGURA_CM),
                layoutConfig.centimetrosParaPontos(LOGO_ALTURA_CM)
        );

        imagem.setAlignment(Element.ALIGN_CENTER);

        documento.add(imagem);
    }

    private void adicionarTitulo(Document documento) {

        Paragraph titulo = new Paragraph("Declaração", fonteTitulo());

        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(
                layoutConfig.espacamentoTituloDepoisPdf()
        );

        documento.add(titulo);
    }

    private void adicionarTextoPrincipal(
            Document documento,
            DeclaracaoComparecimento declaracao
    ) {

        String nomePaciente =
                declaracao.getPaciente().getNomeCompleto();

        String dataComparecimento =
                formatarDataPorExtenso(
                        declaracao.getDataComparecimento()
                );

        String texto;

        if (OrientacaoDeclaracaoComparecimento.RETORNAR_AO_TRABALHO
                .equals(declaracao.getOrientacao())) {

            String horaInicio = formatarHora(
                    declaracao.getHoraInicio()
            );

            String horaTermino = formatarHora(
                    declaracao.getHoraTermino()
            );

            texto = "Declaro para fins de comprovação, que "
                    + nomePaciente
                    + " compareceu ao consultório no dia "
                    + dataComparecimento
                    + ", no horário das "
                    + horaInicio
                    + " às "
                    + horaTermino
                    + " para Consulta Terapêutica.";

        } else {

            texto = "Declaro para fins de comprovação, que "
                    + nomePaciente
                    + " compareceu ao consultório no dia "
                    + dataComparecimento
                    + " para Consulta Terapêutica.";
        }

        Paragraph paragrafo = new Paragraph(texto, fonteTextoItalico());

        paragrafo.setAlignment(Element.ALIGN_JUSTIFIED);
        paragrafo.setLeading(0, 1.35f);
        paragrafo.setSpacingBefore(
                layoutConfig.espacamentoCorpoAntesPdf()
        );
        paragrafo.setSpacingAfter(
                layoutConfig.espacamentoCorpoDepoisPdf()
        );

        documento.add(paragrafo);
    }

    private void adicionarOrientacao(
            Document documento,
            DeclaracaoComparecimento declaracao
    ) {

        Paragraph titulo = new Paragraph(
                "Foi orientado a:",
                fonteTextoNegritoItalico()
        );

        titulo.setAlignment(Element.ALIGN_LEFT);
        titulo.setSpacingAfter(12f);

        documento.add(titulo);

        boolean retornarAoTrabalho =
                OrientacaoDeclaracaoComparecimento.RETORNAR_AO_TRABALHO
                        .equals(declaracao.getOrientacao());

        boolean permanecerEmRepouso =
                OrientacaoDeclaracaoComparecimento.PERMANECER_EM_REPOUSO
                        .equals(declaracao.getOrientacao());

        adicionarLinhaOrientacao(
                documento,
                retornarAoTrabalho,
                "Retornar ao trabalho"
        );

        String textoRepouso = "Permanecer em repouso:";

        if (permanecerEmRepouso
                && declaracao.getQuantidadeDiasRepouso() != null) {

            textoRepouso += " por "
                    + declaracao.getQuantidadeDiasRepouso()
                    + " dia(s)";
        }

        adicionarLinhaOrientacao(
                documento,
                permanecerEmRepouso,
                textoRepouso
        );
    }

    private void adicionarLinhaOrientacao(
            Document documento,
            boolean selecionado,
            String texto
    ) {

        // Marcadores textuais mantêm compatibilidade com as fontes
        // disponíveis no gerador PDF.
        String marcador = selecionado ? "[X] " : "[ ] ";

        Paragraph paragrafo = new Paragraph(
                marcador + texto,
                fonteTextoItalico()
        );

        paragrafo.setAlignment(Element.ALIGN_LEFT);
        paragrafo.setSpacingAfter(
                layoutConfig.espacamentoDepoisItemHipotesePdf()
        );

        documento.add(paragrafo);
    }

    private void adicionarLinhasComentario(
            Document documento,
            int quantidade
    ) {

        for (int i = 0; i < quantidade; i++) {

            Paragraph linha = new Paragraph(
                    "____________________________________________________________",
                    fonteTextoItalico()
            );

            linha.setAlignment(Element.ALIGN_LEFT);
            linha.setSpacingAfter(9f);

            documento.add(linha);
        }
    }

    private void adicionarDataEmissao(
            Document documento,
            DeclaracaoComparecimento declaracao
    ) {

        String texto = "São Paulo, "
                + formatarDataPorExtenso(declaracao.getDataEmissao());

        Paragraph paragrafo = new Paragraph(
                texto,
                fonteTextoItalico()
        );

        paragrafo.setAlignment(Element.ALIGN_RIGHT);

        paragrafo.setSpacingBefore(
                layoutConfig.espacamentoAntesDataEmissaoPdf()
        );

        documento.add(paragrafo);
    }

    private void adicionarAssinatura(Document documento) {

        Paragraph linha = new Paragraph(
                "__________________________________________",
                fonteTexto()
        );

        linha.setAlignment(Element.ALIGN_CENTER);
        linha.setSpacingAfter(5f);

        documento.add(linha);

        Paragraph assinatura = new Paragraph(
                "MARCIA REGINA - PSICÓLOGA E NEUROPSICÓLOGA",
                fonteTexto()
        );

        assinatura.setAlignment(Element.ALIGN_CENTER);

        documento.add(assinatura);
    }

    private void adicionarEspaco(
            Document documento,
            float tamanho
    ) {

        Paragraph espaco = new Paragraph(" ");
        espaco.setLeading(tamanho);

        documento.add(espaco);
    }

    private Font fonteTexto() {
        return FontFactory.getFont(
                layoutConfig.fonteTextoPdf(),
                layoutConfig.tamanhoFonteTexto(),
                Font.NORMAL
        );
    }

    private Font fonteTextoItalico() {
        return FontFactory.getFont(
                layoutConfig.fonteTextoPdf(),
                layoutConfig.tamanhoFonteTexto(),
                Font.ITALIC
        );
    }

    private Font fonteTextoNegritoItalico() {
        return FontFactory.getFont(
                layoutConfig.fonteTextoPdf(),
                layoutConfig.tamanhoFonteTexto(),
                Font.BOLDITALIC
        );
    }

    private Font fonteTitulo() {
        return FontFactory.getFont(
                layoutConfig.fonteTextoPdf(),
                layoutConfig.tamanhoFonteTitulo(),
                Font.BOLD
        );
    }

    private String formatarDataPorExtenso(LocalDate data) {

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern(
                "d 'de' MMMM 'de' yyyy",
                new Locale("pt", "BR")
        );

        return data.format(formatador);
    }

    private String formatarHora(LocalTime hora) {

        if (hora == null) {
            return "";
        }

        return hora.format(
                DateTimeFormatter.ofPattern("HH:mm")
        );
    }

    public String montarNomeArquivo(
            DeclaracaoComparecimento declaracao
    ) {

        String data = declaracao.getDataEmissao()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        String nomePaciente = normalizarNomeArquivo(
                declaracao.getPaciente().getNomeCompleto()
        );

        return "Declaracao-" + data + "-"
                + nomePaciente + ".pdf";
    }

    private String normalizarNomeArquivo(String valor) {

        if (valor == null || valor.trim().isEmpty()) {
            return "paciente";
        }

        String semAcento = Normalizer.normalize(
                valor.trim(),
                Normalizer.Form.NFD
        ).replaceAll(
                "[\\p{InCombiningDiacriticalMarks}]",
                ""
        );

        return semAcento
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("\\s+", "_");
    }
}
