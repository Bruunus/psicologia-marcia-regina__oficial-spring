
package br.com.psicologia.marcia.service.acompanhamento;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.imageio.ImageIO;

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

import br.com.psicologia.marcia.DTO.acompanhamento.RelatorioAtendimentoDownloadRequest;
import br.com.psicologia.marcia.model.AcompanhamentoPaciente;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.model.enums.TipoPapelDocumento;
import br.com.psicologia.marcia.repository.acompanhamento.AcompanhamentoPacienteRepository;
import br.com.psicologia.marcia.service.documento.ConfiguracaoDocumento;
import br.com.psicologia.marcia.service.documento.DocumentoLayoutConfig;

@Service
public class RelatorioAtendimentoWordService {

    private static final int TAMANHO_FONTE_PADRAO = 11;
    private static final int TAMANHO_FONTE_TITULO = 14;

    private static final double ESPACAMENTO_ENTRE_LINHAS = 1.15;

    private static final String CAMINHO_LOGO =
            "documentos/imagens/logo-completo.png";

    private static final String ASSINATURA =
            "MARCIA REGINA - PSICÓLOGA E NEUROPSICÓLOGA";

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
    private final ConfiguracaoDocumento configuracaoDocumento;
    private final DocumentoLayoutConfig layoutConfig;

    public RelatorioAtendimentoWordService(
            AcompanhamentoPacienteRepository repository,
            ConfiguracaoDocumento configuracaoDocumento,
            DocumentoLayoutConfig layoutConfig
    ) {
        this.repository = repository;
        this.configuracaoDocumento = configuracaoDocumento;
        this.layoutConfig = layoutConfig;
    }

    @Transactional(readOnly = true)
    public byte[] gerarWord(
            RelatorioAtendimentoDownloadRequest request
    ) {

        if (request == null || request.acompanhamentoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ID do acompanhamento é obrigatório."
            );
        }

        TipoPapelDocumento tipoPapel =
                validarTipoPapel(request.tipoPapel());

        AcompanhamentoPaciente acompanhamento = repository
                .findByIdAndStatusDelete(
                        request.acompanhamentoId(),
                        StatusDelete.NAO_DELETADO
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Acompanhamento não encontrado."
                ));

        try (
                XWPFDocument documento = new XWPFDocument();
                ByteArrayOutputStream saida = new ByteArrayOutputStream()
        ) {

            configuracaoDocumento.configurar(
                    documento,
                    tipoPapel
            );

            adicionarLogotipo(documento);
            adicionarTitulo(documento);
            adicionarIdentificacao(documento, acompanhamento);
            adicionarConteudo(documento, acompanhamento);
            adicionarDataEmissao(documento, acompanhamento);
            adicionarAssinatura(documento);

            documento.write(saida);

            return saida.toByteArray();

        } catch (ResponseStatusException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível gerar o Word do relatório de atendimento.",
                    exception
            );
        }
    }

    private TipoPapelDocumento validarTipoPapel(String valor) {

        if (valor == null || valor.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O tipo de papel é obrigatório."
            );
        }

        try {
            TipoPapelDocumento tipoPapel =
                    TipoPapelDocumento.valueOf(
                            valor.trim().toUpperCase(Locale.ROOT)
                    );

            return configuracaoDocumento.normalizarTipoPapel(
                    tipoPapel
            );

        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de papel inválido. Use A4 ou CARTA."
            );
        }
    }

    private void adicionarLogotipo(XWPFDocument documento)
            throws Exception {

        ClassPathResource recurso =
                new ClassPathResource(CAMINHO_LOGO);

        if (!recurso.exists()) {
            throw new IllegalStateException(
                    "Logotipo não encontrado: " + CAMINHO_LOGO
            );
        }

        BufferedImage imagem;

        try (InputStream leitura = recurso.getInputStream()) {
            imagem = ImageIO.read(leitura);
        }

        if (imagem == null) {
            throw new IllegalStateException(
                    "Não foi possível interpretar o logotipo."
            );
        }

        double larguraCm = 4.0;

        double alturaCm = larguraCm
                * imagem.getHeight()
                / imagem.getWidth();

        XWPFParagraph paragrafo = documento.createParagraph();

        paragrafo.setAlignment(ParagraphAlignment.CENTER);
        paragrafo.setSpacingBefore(0);
        paragrafo.setSpacingAfter(940);

        XWPFRun run = paragrafo.createRun();

        try (InputStream dados = recurso.getInputStream()) {
            run.addPicture(
                    dados,
                    Document.PICTURE_TYPE_PNG,
                    "logo-completo.png",
                    layoutConfig.centimetrosParaEmu(larguraCm),
                    layoutConfig.centimetrosParaEmu(alturaCm)
            );
        }
    }

    private void adicionarTitulo(XWPFDocument documento) {

        XWPFParagraph paragrafo = documento.createParagraph();

        paragrafo.setAlignment(ParagraphAlignment.CENTER);
        paragrafo.setSpacingBefore(0);
        paragrafo.setSpacingAfter(700);

        XWPFRun run = paragrafo.createRun();

        aplicarFontePadrao(run);

        run.setFontSize(TAMANHO_FONTE_TITULO);
        run.setBold(true);
        run.setText("Relatório de Atendimento");
    }

    private void adicionarIdentificacao(
            XWPFDocument documento,
            AcompanhamentoPaciente acompanhamento
    ) {

        String nomePaciente =
                acompanhamento.getPaciente() != null
                        ? acompanhamento.getPaciente().getNomeCompleto()
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
            XWPFDocument documento,
            String rotulo,
            String valor
    ) {

        XWPFParagraph paragrafo = documento.createParagraph();

        paragrafo.setAlignment(ParagraphAlignment.LEFT);
        paragrafo.setSpacingBefore(0);
        paragrafo.setSpacingAfter(120);

        XWPFRun rotuloRun = paragrafo.createRun();

        aplicarFontePadrao(rotuloRun);
        rotuloRun.setBold(true);
        rotuloRun.setText(rotulo);

        XWPFRun valorRun = paragrafo.createRun();

        aplicarFontePadrao(valorRun);
        valorRun.setText(valor == null ? "" : valor);
    }

    private void adicionarConteudo(
            XWPFDocument documento,
            AcompanhamentoPaciente acompanhamento
    ) {

        XWPFParagraph titulo = documento.createParagraph();

        titulo.setAlignment(ParagraphAlignment.LEFT);
        titulo.setSpacingBefore(560);
        titulo.setSpacingAfter(250);

        XWPFRun tituloRun = titulo.createRun();

        aplicarFontePadrao(tituloRun);
        tituloRun.setBold(true);
        tituloRun.setText("Acompanhamento");

        String texto =
                acompanhamento.getAcompanhamento() == null
                        ? ""
                        : acompanhamento.getAcompanhamento();

        XWPFParagraph paragrafo = documento.createParagraph();

        paragrafo.setAlignment(ParagraphAlignment.BOTH);

        paragrafo.setIndentationFirstLine(
                layoutConfig.recuoPrimeiraLinhaCorpoTwips()
        );

        paragrafo.setSpacingBetween(
                ESPACAMENTO_ENTRE_LINHAS
        );

        paragrafo.setSpacingBefore(0);
        paragrafo.setSpacingAfter(200);

        XWPFRun textoRun = paragrafo.createRun();

        aplicarFontePadrao(textoRun);

        adicionarTextoComQuebras(
                textoRun,
                texto
        );
    }

    private void adicionarDataEmissao(
            XWPFDocument documento,
            AcompanhamentoPaciente acompanhamento
    ) {

        LocalDateTime dataHora =
                acompanhamento.getDataAcompanhamento();

        LocalDate data =
                dataHora != null
                        ? dataHora.toLocalDate()
                        : null;

        String dataFormatada =
                data == null
                        ? "Data não informada"
                        : data.format(FORMATO_DATA_EXTENSO);

        XWPFParagraph paragrafo = documento.createParagraph();

        paragrafo.setAlignment(ParagraphAlignment.RIGHT);
        paragrafo.setSpacingBefore(900);
        paragrafo.setSpacingAfter(0);

        XWPFRun run = paragrafo.createRun();

        aplicarFontePadrao(run);
        run.setText("São Paulo, " + dataFormatada);
    }

    private void adicionarAssinatura(XWPFDocument documento) {

        XWPFParagraph linha = documento.createParagraph();

        linha.setAlignment(ParagraphAlignment.CENTER);
        linha.setSpacingBefore(1350);
        linha.setSpacingAfter(0);

        XWPFRun linhaRun = linha.createRun();

        aplicarFontePadrao(linhaRun);

        linhaRun.setText(
                "______________________________________________"
        );

        XWPFParagraph nome = documento.createParagraph();

        nome.setAlignment(ParagraphAlignment.CENTER);
        nome.setSpacingBefore(0);
        nome.setSpacingAfter(0);

        XWPFRun nomeRun = nome.createRun();

        aplicarFontePadrao(nomeRun);
        nomeRun.setText(ASSINATURA);
    }

    // Mesmo padrão de aplicação de fonte do Laudo.
    private void aplicarFontePadrao(XWPFRun run) {

        run.setFontFamily(
                layoutConfig.fonteTexto()
        );

        run.setFontSize(
                TAMANHO_FONTE_PADRAO
        );
    }

    private void adicionarTextoComQuebras(
            XWPFRun run,
            String texto
    ) {

        String[] linhas = texto.split("\\R", -1);

        for (int i = 0; i < linhas.length; i++) {

            if (i > 0) {
                run.addBreak();
            }

            run.setText(linhas[i]);
        }
    }
}
