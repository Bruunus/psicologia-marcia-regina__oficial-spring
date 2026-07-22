package br.com.psicologia.marcia.service.declaracao;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.psicologia.marcia.DTO.declaracao.DeclaracaoComparecimentoWordArquivo;
import br.com.psicologia.marcia.DTO.declaracao.DeclaracaoComparecimentoWordRequest;
import br.com.psicologia.marcia.model.DeclaracaoComparecimento;
import br.com.psicologia.marcia.model.enums.OrientacaoDeclaracaoComparecimento;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.model.enums.TipoPapelDocumento;
import br.com.psicologia.marcia.repository.declaracao.DeclaracaoComparecimentoRepository;
import br.com.psicologia.marcia.service.documento.ConfiguracaoDocumento;
import br.com.psicologia.marcia.service.documento.DocumentoLayoutConfig;

@Service
public class DeclaracaoComparecimentoWordService {

    private static final String CAMINHO_LOGO_DECLARACAO = "documentos/imagens/logo-completo.png";
    private static final double LOGO_DECLARACAO_LARGURA_CM = 4.10;
    private static final double LOGO_DECLARACAO_ALTURA_CM = 3.93;

    private final DeclaracaoComparecimentoRepository declaracaoComparecimentoRepository;
    private final ConfiguracaoDocumento configuracaoDocumento;
    private final DocumentoLayoutConfig documentoLayoutConfig;

    public DeclaracaoComparecimentoWordService(
            DeclaracaoComparecimentoRepository declaracaoComparecimentoRepository,
            ConfiguracaoDocumento configuracaoDocumento,
            DocumentoLayoutConfig documentoLayoutConfig
    ) {
        this.declaracaoComparecimentoRepository = declaracaoComparecimentoRepository;
        this.configuracaoDocumento = configuracaoDocumento;
        this.documentoLayoutConfig = documentoLayoutConfig;
    }

    @Transactional(readOnly = true)
    public DeclaracaoComparecimentoWordArquivo baixarWord(DeclaracaoComparecimentoWordRequest request) {
        validarRequest(request);

        DeclaracaoComparecimento declaracao = declaracaoComparecimentoRepository
                .findByIdAndStatusDelete(request.getDeclaracaoId(), StatusDelete.NAO_DELETADO)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Declaração não encontrada."
                ));

        TipoPapelDocumento tipoPapel = converterTipoPapel(request.getTipoPapel());

        byte[] conteudo = gerarDocumento(declaracao, tipoPapel);
        String nomeArquivo = montarNomeArquivo(declaracao);

        return new DeclaracaoComparecimentoWordArquivo(nomeArquivo, conteudo);
    }

    private void validarRequest(DeclaracaoComparecimentoWordRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dados para download da declaração não foram enviados."
            );
        }

        if (request.getDeclaracaoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID da declaração é obrigatório."
            );
        }

        if (request.getTipoPapel() == null || request.getTipoPapel().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de papel é obrigatório."
            );
        }

        converterTipoPapel(request.getTipoPapel());
    }

    private TipoPapelDocumento converterTipoPapel(String tipoPapel) {
        String valor = tipoPapel.trim().toUpperCase(Locale.ROOT);

        try {
            return configuracaoDocumento.normalizarTipoPapel(TipoPapelDocumento.valueOf(valor));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de papel inválido. Use A4 ou CARTA."
            );
        }
    }

    private byte[] gerarDocumento(DeclaracaoComparecimento declaracao, TipoPapelDocumento tipoPapel) {
        try (XWPFDocument documento = new XWPFDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            configuracaoDocumento.configurar(documento, tipoPapel);
            ajustarMargemSuperiorDeclaracao(documento);

            adicionarLogo(documento);
            adicionarEspaco(documento, 3);
            adicionarTitulo(documento);
            adicionarEspaco(documento, 1);
            adicionarTextoPrincipal(documento, declaracao);
            adicionarEspaco(documento, 1);
            adicionarOrientacao(documento, declaracao);
            adicionarEspaco(documento, 1);
            adicionarDataEmissao(documento, declaracao);
            adicionarEspaco(documento, 2);
            adicionarAssinatura(documento);

            documento.write(outputStream);
            return outputStream.toByteArray();

        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível gerar o arquivo Word da declaração.",
                    exception
            );
        }
    }

    private void ajustarMargemSuperiorDeclaracao(XWPFDocument documento) {
        var sectPr = documento.getDocument().getBody().getSectPr();

        if (sectPr == null) {
            sectPr = documento.getDocument().getBody().addNewSectPr();
        }

        var pageMar = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();

        pageMar.setTop(java.math.BigInteger.valueOf(700));
    }

    private void adicionarLogo(XWPFDocument documento) throws Exception {
        ClassPathResource logoResource = new ClassPathResource(CAMINHO_LOGO_DECLARACAO);

        if (!logoResource.exists()) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Logo da declaração não encontrado em: " + CAMINHO_LOGO_DECLARACAO
            );
        }

        XWPFParagraph paragraph = documento.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.setSpacingAfter(0);

        XWPFRun run = paragraph.createRun();

        try (InputStream inputStream = logoResource.getInputStream()) {
            run.addPicture(
                    inputStream,
                    Document.PICTURE_TYPE_PNG,
                    "logo-completo.png",
                    documentoLayoutConfig.centimetrosParaEmu(LOGO_DECLARACAO_LARGURA_CM),
                    documentoLayoutConfig.centimetrosParaEmu(LOGO_DECLARACAO_ALTURA_CM)
            );
        }
    }

    private void adicionarTitulo(XWPFDocument documento) {
        XWPFParagraph paragraph = documento.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        paragraph.setSpacingBefore(0);
        paragraph.setSpacingAfter(documentoLayoutConfig.espacamentoTituloDepoisTwips());

        XWPFRun run = paragraph.createRun();
        aplicarFonteTitulo(run);
        run.setText("Declaração");
        run.setBold(true);
        run.setItalic(false);
    }

    private void adicionarTextoPrincipal(XWPFDocument documento, DeclaracaoComparecimento declaracao) {
        String nomePaciente = declaracao.getPaciente().getNomeCompleto();
        String dataComparecimento = formatarDataPorExtenso(declaracao.getDataComparecimento());

        String texto;

        if (OrientacaoDeclaracaoComparecimento.RETORNAR_AO_TRABALHO.equals(declaracao.getOrientacao())) {
            String horaInicio = formatarHora(declaracao.getHoraInicio());
            String horaTermino = formatarHora(declaracao.getHoraTermino());

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

        XWPFParagraph paragraph = documento.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.BOTH);
        paragraph.setSpacingBefore(documentoLayoutConfig.espacamentoCorpoAntesTwips());
        paragraph.setSpacingAfter(documentoLayoutConfig.espacamentoCorpoDepoisTwips());

        XWPFRun run = paragraph.createRun();
        aplicarFonteTexto(run);
        run.setItalic(true);
        run.setText(texto);
    }

    private void adicionarOrientacao(XWPFDocument documento, DeclaracaoComparecimento declaracao) {
        XWPFParagraph titulo = documento.createParagraph();
        titulo.setAlignment(ParagraphAlignment.LEFT);
        titulo.setSpacingBefore(documentoLayoutConfig.espacamentoAntesHipoteseTituloTwips());
        titulo.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisHipoteseTituloTwips());

        XWPFRun tituloRun = titulo.createRun();
        aplicarFonteTexto(tituloRun);
        tituloRun.setItalic(true);
        tituloRun.setBold(true);
        tituloRun.setText("Foi orientado a:");

        boolean retornarAoTrabalho = OrientacaoDeclaracaoComparecimento.RETORNAR_AO_TRABALHO
                .equals(declaracao.getOrientacao());

        boolean permanecerEmRepouso = OrientacaoDeclaracaoComparecimento.PERMANECER_EM_REPOUSO
                .equals(declaracao.getOrientacao());

        adicionarLinhaOrientacao(
                documento,
                retornarAoTrabalho ? "●" : "○",
                "Retornar ao trabalho"
        );

        String textoRepouso = "Permanecer em repouso:";

        if (permanecerEmRepouso && declaracao.getQuantidadeDiasRepouso() != null) {
            textoRepouso += " por " + declaracao.getQuantidadeDiasRepouso() + " dia(s)";
        }

        adicionarLinhaOrientacao(
                documento,
                permanecerEmRepouso ? "●" : "○",
                textoRepouso
        );

        adicionarEspaco(documento, 1);
        adicionarLinhasComentario(documento, 4);
    }

    private void adicionarLinhaOrientacao(XWPFDocument documento, String marcador, String texto) {
        XWPFParagraph paragraph = documento.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        paragraph.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisItemHipoteseTwips());

        XWPFRun run = paragraph.createRun();
        aplicarFonteTexto(run);
        run.setItalic(true);
        run.setText(marcador + " " + texto);
    }

    private void adicionarLinhasComentario(XWPFDocument documento, int quantidadeLinhas) {
        for (int i = 0; i < quantidadeLinhas; i++) {
            adicionarLinhaComentario(documento);
        }
    }

    private void adicionarLinhaComentario(XWPFDocument documento) {
        XWPFParagraph paragraph = documento.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        paragraph.setSpacingBefore(0);
        paragraph.setSpacingAfter(180);

        XWPFRun run = paragraph.createRun();
        aplicarFonteTexto(run);
        run.setItalic(true);
        run.setText("_________________________________________________________________________________");
    }

    private void adicionarDataEmissao(XWPFDocument documento, DeclaracaoComparecimento declaracao) {
        XWPFParagraph paragraph = documento.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.RIGHT);
        paragraph.setSpacingBefore(documentoLayoutConfig.espacamentoAntesDataEmissaoTwips());
        paragraph.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisDataEmissaoTwips());

        XWPFRun run = paragraph.createRun();
        aplicarFonteTexto(run);
        run.setItalic(true);
        run.setText("São Paulo, " + formatarDataPorExtenso(declaracao.getDataEmissao()));
    }

    private void adicionarAssinatura(XWPFDocument documento) {
        XWPFParagraph linhaAssinatura = documento.createParagraph();
        linhaAssinatura.setAlignment(ParagraphAlignment.CENTER);
        linhaAssinatura.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisLinhaAssinaturaTwips());

        XWPFRun linhaAssinaturaRun = linhaAssinatura.createRun();
        aplicarFonteTexto(linhaAssinaturaRun);
        linhaAssinaturaRun.setItalic(false);
        linhaAssinaturaRun.setText("__________________________________________");

        XWPFParagraph assinatura = documento.createParagraph();
        assinatura.setAlignment(ParagraphAlignment.CENTER);
        assinatura.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisNomeAssinaturaTwips());

        XWPFRun assinaturaRun = assinatura.createRun();
        aplicarFonteTexto(assinaturaRun);
        assinaturaRun.setItalic(false);
        assinaturaRun.setBold(false);
        assinaturaRun.setText("CLINICA PSICOTERAPÊUTICA REFLORESCER LTDA.");
    }

    private void adicionarEspaco(XWPFDocument documento, int quantidadeLinhas) {
        for (int i = 0; i < quantidadeLinhas; i++) {
            XWPFParagraph paragraph = documento.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.addBreak();
        }
    }

    private void aplicarFonteTexto(XWPFRun run) {
        run.setFontFamily(documentoLayoutConfig.fonteTexto());
        run.setFontSize(documentoLayoutConfig.tamanhoFonteTexto());
    }

    private void aplicarFonteTitulo(XWPFRun run) {
        run.setFontFamily(documentoLayoutConfig.fonteTexto());
        run.setFontSize(documentoLayoutConfig.tamanhoFonteTitulo());
    }

    private String formatarDataPorExtenso(LocalDate data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "d 'de' MMMM 'de' yyyy",
                new Locale("pt", "BR")
        );

        return data.format(formatter);
    }

    private String formatarHora(LocalTime hora) {
        if (hora == null) {
            return "";
        }

        return hora.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String montarNomeArquivo(DeclaracaoComparecimento declaracao) {
        String data = declaracao.getDataEmissao().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String nomePaciente = normalizarNomeArquivo(declaracao.getPaciente().getNomeCompleto());

        return "Declaracao-" + data + "-" + nomePaciente + ".docx";
    }

    private String normalizarNomeArquivo(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "paciente";
        }

        String semAcento = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        return semAcento
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("\\s+", "_");
    }

    public String codificarNomeArquivo(String nomeArquivo) {
        return URLEncoder.encode(nomeArquivo, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}