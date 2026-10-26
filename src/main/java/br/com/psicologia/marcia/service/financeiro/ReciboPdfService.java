
package br.com.psicologia.marcia.service.financeiro;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
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
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import br.com.psicologia.marcia.DTO.financeiro.ReciboVisualizarRequest;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.PagamentoPaciente;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.model.enums.StatusPagamento;
import br.com.psicologia.marcia.repository.financeiro.PagamentoPacienteRepository;
import br.com.psicologia.marcia.service.documento.DocumentoLayoutConfig;

@Service
public class ReciboPdfService {

    private static final String CAMINHO_LOGO =
            "documentos/imagens/logo-completo.png";

    private static final String ASSINATURA =
            "MARCIA REGINA - PSICÓLOGA E NEUROPSICÓLOGA";

    private static final int TAMANHO_FONTE_PADRAO = 11;
    private static final int TAMANHO_FONTE_TITULO = 14;

    private static final Color COR_CINZA =
            new Color(242, 242, 242);

    private static final Color COR_CINZA_CLARO =
            new Color(248, 248, 248);

    private static final Color COR_CABECALHO =
            new Color(230, 230, 230);

    private static final Color COR_BORDA =
            new Color(208, 208, 208);

    private static final Color COR_VERDE =
            new Color(35, 122, 67);

    private static final Color COR_VERDE_CLARO =
            new Color(232, 245, 236);

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FORMATO_EXTENSO =
            DateTimeFormatter.ofPattern(
                    "d 'de' MMMM 'de' yyyy",
                    Locale.forLanguageTag("pt-BR")
            );

    private static final Locale LOCALE_BRASIL =
            Locale.forLanguageTag("pt-BR");

    private final PagamentoPacienteRepository repository;
    private final DocumentoLayoutConfig layoutConfig;

    public ReciboPdfService(
            PagamentoPacienteRepository repository,
            DocumentoLayoutConfig layoutConfig
    ) {
        this.repository = repository;
        this.layoutConfig = layoutConfig;
    }

    @Transactional(readOnly = true)
    public byte[] gerarPdf(ReciboVisualizarRequest request) {

        if (request == null || request.pagamentoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ID do pagamento é obrigatório."
            );
        }

        PagamentoPaciente pagamento = repository
                .findByIdAndStatusDelete(
                        request.pagamentoId(),
                        StatusDelete.NAO_DELETADO
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pagamento não encontrado."
                ));

        if (!StatusPagamento.PAGO.equals(
                pagamento.getStatusPagamento()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Somente pagamentos quitados podem gerar recibos."
            );
        }

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
                adicionarQuadroRecibo(documento, pagamento);
                adicionarDadosPaciente(documento, pagamento);
                adicionarDeclaracaoRecebimento(documento, pagamento);
                adicionarDetalhesPagamento(documento, pagamento);
                adicionarStatus(documento);
                adicionarData(documento, pagamento);
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
                    "Não foi possível gerar o PDF do recibo.",
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
                "Recibo de Pagamento",
                fonteTitulo()
        );

        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingBefore(0);
        titulo.setSpacingAfter(650 / 20f);

        documento.add(titulo);
    }

    private void adicionarQuadroRecibo(
            Document documento,
            PagamentoPaciente pagamento
    ) throws DocumentException {

        PdfPTable tabela = new PdfPTable(2);

        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{50, 50});
        tabela.setSpacingAfter(260 / 20f);

        String numero = String.format(
                Locale.ROOT,
                "%06d",
                pagamento.getId()
        );

        Phrase esquerda = new Phrase();
        esquerda.add(new Chunk("Recibo: ", fonteTexto()));
        esquerda.add(new Chunk(
                "Nº " + numero,
                fonteTextoNegrito()
        ));

        Phrase direita = new Phrase();
        direita.add(new Chunk("Pagamento: ", fonteTexto()));
        direita.add(new Chunk(
                formatarData(pagamento.getDtBaixaPagamento()),
                fonteTextoNegrito()
        ));

        PdfPCell celulaEsquerda = criarCelula(
                esquerda,
                Element.ALIGN_LEFT,
                COR_CINZA,
                7
        );

        PdfPCell celulaDireita = criarCelula(
                direita,
                Element.ALIGN_RIGHT,
                COR_CINZA,
                7
        );

        celulaEsquerda.setBorder(
                PdfPCell.TOP | PdfPCell.BOTTOM | PdfPCell.LEFT
        );

        celulaDireita.setBorder(
                PdfPCell.TOP | PdfPCell.BOTTOM | PdfPCell.RIGHT
        );

        celulaEsquerda.setBorderColor(new Color(184, 184, 184));
        celulaDireita.setBorderColor(new Color(184, 184, 184));

        celulaEsquerda.setBorderWidth(1f);
        celulaDireita.setBorderWidth(1f);

        tabela.addCell(celulaEsquerda);
        tabela.addCell(celulaDireita);

        documento.add(tabela);
    }

    private void adicionarDadosPaciente(
            Document documento,
            PagamentoPaciente pagamento
    ) throws DocumentException {

        adicionarSubtitulo(documento, "Dados do paciente", 8);

        PdfPTable tabela = new PdfPTable(1);

        tabela.setWidthPercentage(100);
        tabela.setSpacingAfter(220 / 20f);

        tabela.addCell(criarLinhaPaciente(
                "Nome",
                obterNomePaciente(pagamento)
        ));

        tabela.addCell(criarLinhaPaciente(
                "CPF",
                obterCpfPaciente(pagamento)
        ));

        documento.add(tabela);
    }

    private PdfPCell criarLinhaPaciente(
            String rotulo,
            String valor
    ) {

        Phrase frase = new Phrase();

        Font fonteRotulo = fonteTexto();
        fonteRotulo.setColor(new Color(102, 102, 102));

        frase.add(new Chunk(rotulo + "    ", fonteRotulo));
        frase.add(new Chunk(valor, fonteTextoNegrito()));

        PdfPCell celula = criarCelula(
                frase,
                Element.ALIGN_LEFT,
                Color.WHITE,
                7
        );

        celula.setBorderColor(COR_BORDA);
        celula.setBorderWidth(0.5f);

        return celula;
    }

    private void adicionarDeclaracaoRecebimento(
            Document documento,
            PagamentoPaciente pagamento
    ) throws DocumentException {

        Paragraph paragrafo = new Paragraph();

        paragrafo.setAlignment(Element.ALIGN_JUSTIFIED);
        paragrafo.setFirstLineIndent(
                layoutConfig.recuoPrimeiraLinhaCorpoTwips() / 20f
        );
        paragrafo.setLeading(0, 1.15f);
        paragrafo.setSpacingBefore(180 / 20f);
        paragrafo.setSpacingAfter(380 / 20f);

        paragrafo.add(new Chunk(
                "Recebemos de ",
                fonteTexto()
        ));

        paragrafo.add(new Chunk(
                obterNomePaciente(pagamento),
                fonteTextoNegrito()
        ));

        paragrafo.add(new Chunk(
                " a importância de ",
                fonteTexto()
        ));

        paragrafo.add(new Chunk(
                formatarMoeda(calcularValorTotal(pagamento)),
                fonteTextoNegrito()
        ));

        paragrafo.add(new Chunk(
                " referente ao atendimento terapêutico descrito "
                + "neste recibo, dando plena quitação do pagamento realizado.",
                fonteTexto()
        ));

        documento.add(paragrafo);
    }

    private void adicionarDetalhesPagamento(
            Document documento,
            PagamentoPaciente pagamento
    ) throws DocumentException {

        adicionarSubtitulo(documento, "Detalhes do pagamento", 8);

        PdfPTable tabela = new PdfPTable(4);

        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{
                30f, 17f, 34f, 19f
        });
        tabela.setSpacingAfter(360 / 20f);
        tabela.setHeaderRows(1);

        String[] colunas = {
                "Descrição",
                "Data",
                "Forma de pagamento",
                "Valor"
        };

        for (String coluna : colunas) {
            PdfPCell celula = criarCelula(
                    new Phrase(coluna, fonteTextoNegrito()),
                    Element.ALIGN_LEFT,
                    COR_CABECALHO,
                    7
            );

            tabela.addCell(celula);
        }

        Integer quantidade = pagamento.getQuantidadeSessao();

        String descricao = quantidade != null && quantidade > 1
                ? "Consulta terapêutica (" + quantidade + " sessões)"
                : "Consulta terapêutica";

        tabela.addCell(criarCelula(
                new Phrase(descricao, fonteTexto()),
                Element.ALIGN_LEFT,
                Color.WHITE,
                7
        ));

        tabela.addCell(criarCelula(
                new Phrase(
                        formatarData(pagamento.getDtDoAtendimento()),
                        fonteTexto()
                ),
                Element.ALIGN_LEFT,
                Color.WHITE,
                7
        ));

        tabela.addCell(criarCelula(
                new Phrase(
                        formatarTipoPagamento(pagamento),
                        fonteTexto()
                ),
                Element.ALIGN_LEFT,
                Color.WHITE,
                7
        ));

        tabela.addCell(criarCelula(
                new Phrase(
                        formatarMoeda(calcularValorTotal(pagamento)),
                        fonteTextoNegrito()
                ),
                Element.ALIGN_RIGHT,
                Color.WHITE,
                7
        ));

        // Rodapé com duas extremidades e sem divisão central.
        PdfPTable rodape = new PdfPTable(2);

        rodape.setWidthPercentage(100);
        rodape.setWidths(new float[]{50, 50});

        PdfPCell esquerda = criarCelula(
                new Phrase("Total pago", fonteTextoNegrito()),
                Element.ALIGN_LEFT,
                Color.WHITE,
                10
        );

        PdfPCell direita = criarCelula(
                new Phrase(
                        formatarMoeda(calcularValorTotal(pagamento)),
                        fonteTextoNegrito()
                ),
                Element.ALIGN_RIGHT,
                Color.WHITE,
                10
        );

        esquerda.setBorder(PdfPCell.NO_BORDER);
        direita.setBorder(PdfPCell.NO_BORDER);

        rodape.addCell(esquerda);
        rodape.addCell(direita);

        PdfPCell total = new PdfPCell(rodape);

        total.setColspan(4);
        total.setBorderColor(COR_BORDA);
        total.setBorderWidth(0.5f);
        total.setPadding(0);
        total.setMinimumHeight(32.5f);
        total.setVerticalAlignment(Element.ALIGN_MIDDLE);

        tabela.addCell(total);

        documento.add(tabela);
    }

    private void adicionarStatus(Document documento)
            throws DocumentException {

        PdfPTable tabela = new PdfPTable(2);

        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{87f, 13f});

        PdfPCell texto = criarCelula(
                new Phrase(
                        "Status do pagamento",
                        fonteTextoNegrito()
                ),
                Element.ALIGN_LEFT,
                COR_CINZA_CLARO,
                10
        );

        PdfPCell status = criarCelula(
                new Phrase(
                        "PAGO",
                        fonteStatus()
                ),
                Element.ALIGN_CENTER,
                COR_VERDE_CLARO,
                10
        );

        texto.setMinimumHeight(37.5f);
        status.setMinimumHeight(37.5f);

        texto.setBorderColor(new Color(85, 85, 85));
        status.setBorderColor(new Color(85, 85, 85));

        texto.setBorderWidth(0.75f);
        status.setBorderWidth(0.75f);

        tabela.addCell(texto);
        tabela.addCell(status);

        documento.add(tabela);
    }

    private void adicionarData(
            Document documento,
            PagamentoPaciente pagamento
    ) throws DocumentException {

        LocalDate data = pagamento.getDtBaixaPagamento();

        String dataExtenso = data == null
                ? "Data não informada"
                : data.format(FORMATO_EXTENSO);

        Paragraph paragrafo = new Paragraph(
                "São Paulo, " + dataExtenso,
                fonteTexto()
        );

        paragrafo.setAlignment(Element.ALIGN_RIGHT);
        paragrafo.setSpacingBefore(600 / 20f);
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
        linha.setSpacingBefore(1050 / 20f);
        linha.setSpacingAfter(0);

        documento.add(linha);

        Paragraph nome = new Paragraph(
                ASSINATURA,
                fonteTexto()
        );

        nome.setAlignment(Element.ALIGN_CENTER);
        nome.setSpacingBefore(0);
        nome.setSpacingAfter(2);

        documento.add(nome);

        Font fonteLegenda = fonteTexto();
        fonteLegenda.setSize(9);
        fonteLegenda.setColor(new Color(102, 102, 102));

        Paragraph legenda = new Paragraph(
                "Responsável pelo recebimento",
                fonteLegenda
        );

        legenda.setAlignment(Element.ALIGN_CENTER);
        legenda.setSpacingBefore(0);
        legenda.setSpacingAfter(0);

        documento.add(legenda);
    }

    private PdfPCell criarCelula(
            Phrase conteudo,
            int alinhamento,
            Color fundo,
            float paddingVertical
    ) {
        PdfPCell celula = new PdfPCell(conteudo);

        celula.setHorizontalAlignment(alinhamento);
        celula.setVerticalAlignment(Element.ALIGN_MIDDLE);

        celula.setBackgroundColor(fundo);
        celula.setBorderColor(COR_BORDA);
        celula.setBorderWidth(0.5f);

        celula.setPaddingLeft(7);
        celula.setPaddingRight(7);
        celula.setPaddingTop(paddingVertical);
        celula.setPaddingBottom(paddingVertical);

        return celula;
    }

    private void adicionarSubtitulo(
            Document documento,
            String texto,
            float espacamentoDepois
    ) throws DocumentException {

        Paragraph paragrafo = new Paragraph(
                texto,
                fonteTextoNegrito()
        );

        paragrafo.setAlignment(Element.ALIGN_LEFT);
        paragrafo.setSpacingBefore(160 / 20f);
        paragrafo.setSpacingAfter(espacamentoDepois);

        documento.add(paragrafo);
    }

    private String obterNomePaciente(
            PagamentoPaciente pagamento
    ) {
        Paciente paciente = pagamento.getPaciente();

        if (paciente == null
                || paciente.getNomeCompleto() == null
                || paciente.getNomeCompleto().isBlank()) {
            return "Não informado";
        }

        return paciente.getNomeCompleto();
    }

    private String obterCpfPaciente(
            PagamentoPaciente pagamento
    ) {
        Paciente paciente = pagamento.getPaciente();

        if (paciente == null
                || paciente.getCpf() == null
                || paciente.getCpf().isBlank()) {
            return "Não informado";
        }

        String cpf = paciente.getCpf().trim();
        String digitos = cpf.replaceAll("\\D", "");

        if (digitos.length() == 11) {
            return digitos.substring(0, 3) + "."
                    + digitos.substring(3, 6) + "."
                    + digitos.substring(6, 9) + "-"
                    + digitos.substring(9);
        }

        return cpf;
    }

    private BigDecimal calcularValorTotal(
            PagamentoPaciente pagamento
    ) {
        BigDecimal valor = pagamento.getValorSessao();
        Integer quantidade = pagamento.getQuantidadeSessao();

        if (valor == null || quantidade == null) {
            return null;
        }

        return valor.multiply(
                BigDecimal.valueOf(quantidade)
        );
    }

    private String formatarTipoPagamento(
            PagamentoPaciente pagamento
    ) {
        if (pagamento.getTipoDePagamento() == null) {
            return "Não informado";
        }

        return switch (pagamento.getTipoDePagamento()) {
            case AVULSO -> "Avulso";
            case MENSAL -> "Mensal";
        };
    }

    private String formatarData(LocalDate data) {
        return data == null
                ? "Não informada"
                : data.format(FORMATO_DATA);
    }

    private String formatarMoeda(BigDecimal valor) {
        return valor == null
                ? "Não informado"
                : NumberFormat.getCurrencyInstance(
                        LOCALE_BRASIL
                ).format(valor);
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

    private Font fonteStatus() {
        Font fonte = fonteTextoNegrito();
        fonte.setColor(COR_VERDE);
        return fonte;
    }
}
