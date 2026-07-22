package br.com.psicologia.marcia.service.relatorio.psicologico;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import br.com.psicologia.marcia.DTO.relatorio.psicologico.PdfRelatorioPsicologicoGeradoDTO;
import br.com.psicologia.marcia.DTO.relatorio.psicologico.RelatorioPsicologicoVisualizarRequest;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.Psicologo;
import br.com.psicologia.marcia.model.RelatorioPsicologico;
import br.com.psicologia.marcia.model.RelatorioPsicologicoHipoteseDiagnostica;
import br.com.psicologia.marcia.repository.relatorio.psicologico.RelatorioPsicologicoRepository;
import br.com.psicologia.marcia.service.documento.BlocoIdentificacaoDocumentoService;
import br.com.psicologia.marcia.service.documento.DocumentoLayoutConfig;
import br.com.psicologia.marcia.service.documento.DocumentoPdfCabecalhoEvent;
import br.com.psicologia.marcia.service.psicologo.PsicologoService;

@Service
public class PdfRelatorioPsicologicoService {

    private final RelatorioPsicologicoRepository relatorioPsicologicoRepository;
    private final PsicologoService psicologoService;
    private final DocumentoLayoutConfig documentoLayoutConfig;
    private final BlocoIdentificacaoDocumentoService blocoIdentificacaoDocumentoService;

    public PdfRelatorioPsicologicoService(
            RelatorioPsicologicoRepository relatorioPsicologicoRepository,
            PsicologoService psicologoService,
            DocumentoLayoutConfig documentoLayoutConfig,
            BlocoIdentificacaoDocumentoService blocoIdentificacaoDocumentoService
    ) {
        this.relatorioPsicologicoRepository = relatorioPsicologicoRepository;
        this.psicologoService = psicologoService;
        this.documentoLayoutConfig = documentoLayoutConfig;
        this.blocoIdentificacaoDocumentoService = blocoIdentificacaoDocumentoService;
    }

    @Transactional(readOnly = true)
    public PdfRelatorioPsicologicoGeradoDTO gerarPdf(RelatorioPsicologicoVisualizarRequest request) {
        RelatorioPsicologico relatorio = relatorioPsicologicoRepository.findById(request.relatorioId())
                .orElseThrow(() -> new RuntimeException("Relatório psicológico não encontrado."));

        Paciente paciente = relatorio.getPaciente();
        Psicologo psicologo = psicologoService.buscarPsicologoAtivoPrincipal();

        try (ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            Document documento = new Document(
                    PageSize.A4,
                    documentoLayoutConfig.margemEsquerdaPdf(),
                    documentoLayoutConfig.margemDireitaPdf(),
                    documentoLayoutConfig.margemSuperiorPdf(),
                    documentoLayoutConfig.margemInferiorPdf()
            );

            PdfWriter writer = PdfWriter.getInstance(documento, saida);

            writer.setPageEvent(new DocumentoPdfCabecalhoEvent(documentoLayoutConfig, psicologo));

            documento.open();

            blocoIdentificacaoDocumentoService.criarPdf(documento, paciente);
            criarEspaco(documento, 1);
            criarTitulo(documento);
            criarPrimeiroParagrafo(documento, relatorio);
            criarAnalise(documento, relatorio);
            criarHipotesesDiagnosticas(documento, relatorio);
            criarFraseFinal(documento);
            criarDataEmissao(documento, relatorio);
            criarAssinaturaFixa(documento, writer, psicologo);

            documento.close();

            return new PdfRelatorioPsicologicoGeradoDTO(
                    saida.toByteArray(),
                    montarNomeArquivo(paciente, relatorio)
            );
        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF do relatório psicológico.", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao gerar PDF do relatório psicológico.", e);
        }
    }

    private void criarTitulo(Document documento) {
        Paragraph titulo = new Paragraph("Relatório Psicológico", fonteTitulo());

        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingBefore(documentoLayoutConfig.espacamentoTituloAntesPdf());
        titulo.setSpacingAfter(documentoLayoutConfig.espacamentoTituloDepoisPdf());

        documento.add(titulo);
    }

    private void criarPrimeiroParagrafo(Document documento, RelatorioPsicologico relatorio) {
        Paragraph paragrafo = criarParagrafoCorpo();

        paragrafo.add(new Chunk("Paciente supracitado está em acompanhamento psicoterapêutico desde ", fonteTexto()));
        paragrafo.add(new Chunk(montarDataAcompanhamento(relatorio), fonteTexto()));
        paragrafo.add(new Chunk(",sobre os meus cuidados dentro da perspectiva ", fonteTexto()));
        paragrafo.add(new Chunk(formatarTextoDeCampo(relatorio.getAbordagem()), fonteTexto()));
        paragrafo.add(new Chunk(". Os atendimentos ocorreram ", fonteTexto()));
        paragrafo.add(new Chunk(montarTextoOcorrencia(relatorio.getOcorrencia()), fonteTexto()));
        paragrafo.add(new Chunk(" por semana em formato ", fonteTexto()));
        paragrafo.add(new Chunk(formatarTextoDeCampo(relatorio.getFormato()), fonteTexto()));
        paragrafo.add(new Chunk(".", fonteTexto()));

        documento.add(paragrafo);
    }

    private void criarAnalise(Document documento, RelatorioPsicologico relatorio) {
        Paragraph paragrafo = criarParagrafoCorpo();

        paragrafo.add(new Chunk(valor(relatorio.getAnalise()), fonteTexto()));

        documento.add(paragrafo);
    }

    private void criarHipotesesDiagnosticas(Document documento, RelatorioPsicologico relatorio) {
        Paragraph titulo = new Paragraph("Hipótese diagnóstica:", fonteTextoNegrito());

        titulo.setAlignment(Element.ALIGN_LEFT);
        titulo.setSpacingBefore(documentoLayoutConfig.espacamentoAntesHipoteseTituloPdf());
        titulo.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisHipoteseTituloPdf());

        documento.add(titulo);

        List<RelatorioPsicologicoHipoteseDiagnostica> hipoteses = relatorio.getHipotesesDiagnosticas();

        if (hipoteses == null || hipoteses.isEmpty()) {
            Paragraph vazio = new Paragraph("Nenhuma hipótese diagnóstica informada.", fonteTexto());

            vazio.setIndentationLeft(documentoLayoutConfig.recuoHipotesePdf());
            vazio.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisItemHipotesePdf());

            documento.add(vazio);
            return;
        }

        com.lowagie.text.List lista = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);

        lista.setIndentationLeft(documentoLayoutConfig.recuoHipotesePdf());
        lista.setListSymbol("\u2022 ");

        for (RelatorioPsicologicoHipoteseDiagnostica hipotese : hipoteses) {
            com.lowagie.text.ListItem item = new com.lowagie.text.ListItem(
                    valor(hipotese.getDescricao()),
                    fonteTexto()
            );

            item.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisItemHipotesePdf());

            lista.add(item);
        }

        documento.add(lista);
    }

    private void criarFraseFinal(Document documento) {
        Paragraph paragrafo = new Paragraph("Sem mais para quaisquer dúvidas,estou à disposição.", fonteTexto());

        paragrafo.setAlignment(Element.ALIGN_LEFT);
        paragrafo.setSpacingBefore(documentoLayoutConfig.espacamentoAntesFraseFinalPdf());
        paragrafo.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisFraseFinalPdf());

        documento.add(paragrafo);
    }

    private void criarDataEmissao(Document documento, RelatorioPsicologico relatorio) {
        Paragraph paragrafo = new Paragraph(
                valor(relatorio.getCidadeEmissao()) + "," +
                        valor(relatorio.getDiaEmissao()) + " de " +
                        valor(relatorio.getMesEmissao()) + " de " +
                        valor(relatorio.getAnoEmissao()) + ".",
                fonteTexto()
        );

        paragrafo.setAlignment(Element.ALIGN_RIGHT);
        paragrafo.setSpacingBefore(documentoLayoutConfig.espacamentoAntesDataEmissaoPdf());
        paragrafo.setSpacingAfter(documentoLayoutConfig.espacamentoDepoisDataEmissaoPdf());

        documento.add(paragrafo);
    }

    private void criarAssinaturaFixa(Document documento, PdfWriter writer, Psicologo psicologo) throws DocumentException {
        float posicaoAtual = writer.getVerticalPosition(false);

        if (posicaoAtual <= documentoLayoutConfig.posicaoMinimaAssinaturaPdf()) {
            documento.newPage();
        }

        PdfPTable tabela = new PdfPTable(1);

        tabela.setTotalWidth(documento.right() - documento.left());
        tabela.setLockedWidth(true);

        tabela.addCell(criarCelulaAssinatura(documentoLayoutConfig.linhaAssinatura(), fonteTexto(), 4));
        tabela.addCell(criarCelulaAssinatura(valor(psicologo.getNome()), fonteTexto(), 4));
        tabela.addCell(criarCelulaAssinatura(valor(psicologo.getFuncaoEmpresa()), fonteTexto(), 4));
        tabela.addCell(criarCelulaAssinatura("CRP: " + valor(psicologo.getCrp()), fonteTexto(), 0));

        tabela.writeSelectedRows(
                0,
                -1,
                documento.left(),
                documentoLayoutConfig.posicaoTopoAssinaturaPdf(),
                writer.getDirectContent()
        );
    }

    private PdfPCell criarCelulaAssinatura(String texto, Font fonte, float paddingBottom) {
        PdfPCell celula = new PdfPCell(new Phrase(texto, fonte));

        celula.setBorder(Rectangle.NO_BORDER);
        celula.setHorizontalAlignment(Element.ALIGN_CENTER);
        celula.setPaddingTop(0);
        celula.setPaddingBottom(paddingBottom);

        return celula;
    }

    private Paragraph criarParagrafoCorpo() {
        Paragraph paragrafo = new Paragraph();

        paragrafo.setAlignment(Element.ALIGN_JUSTIFIED);
        paragrafo.setFirstLineIndent(documentoLayoutConfig.recuoPrimeiraLinhaCorpoPdf());
        paragrafo.setLeading(0, 1.35f);
        paragrafo.setSpacingBefore(documentoLayoutConfig.espacamentoCorpoAntesPdf());
        paragrafo.setSpacingAfter(documentoLayoutConfig.espacamentoCorpoDepoisPdf());

        return paragrafo;
    }

    private void criarEspaco(Document documento, int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            Paragraph espaco = new Paragraph(" ");

            espaco.setSpacingBefore(0);
            espaco.setSpacingAfter(0);

            documento.add(espaco);
        }
    }

    private Font fonteTitulo() {
        return FontFactory.getFont(
                documentoLayoutConfig.fonteTextoPdf(),
                documentoLayoutConfig.tamanhoFonteTitulo(),
                Font.BOLD
        );
    }

    private Font fonteTexto() {
        return FontFactory.getFont(
                documentoLayoutConfig.fonteTextoPdf(),
                documentoLayoutConfig.tamanhoFonteTexto(),
                Font.NORMAL
        );
    }

    private Font fonteTextoNegrito() {
        return FontFactory.getFont(
                documentoLayoutConfig.fonteTextoPdf(),
                documentoLayoutConfig.tamanhoFonteTexto(),
                Font.BOLD
        );
    }

    private String montarDataAcompanhamento(RelatorioPsicologico relatorio) {
        String mes = formatarTextoDeCampo(relatorio.getMesAcompanhamento());
        String ano = valor(relatorio.getAnoAcompanhamento());

        if (mes.isBlank() && ano.isBlank()) {
            return "";
        }

        if (mes.isBlank()) {
            return ano;
        }

        if (ano.isBlank()) {
            return mes;
        }

        return mes + " de " + ano;
    }

    private String montarTextoOcorrencia(Integer ocorrencia) {
        if (ocorrencia == null || ocorrencia <= 0) {
            return "";
        }

        if (ocorrencia == 1) {
            return "1 vez";
        }

        return ocorrencia + " vezes";
    }

    private String formatarTextoDeCampo(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }

        return texto
                .trim()
                .replace("-", " ")
                .replace("_", " ")
                .replaceAll("\\s+", " ");
    }

    private String montarNomeArquivo(Paciente paciente, RelatorioPsicologico relatorio) {
        String nomePaciente = "paciente";

        if (paciente != null && paciente.getNomeCompleto() != null && !paciente.getNomeCompleto().isBlank()) {
            nomePaciente = paciente.getNomeCompleto();
        }

        String data = "sem-data";

        if (relatorio.getDataCriacao() != null) {
            data = relatorio.getDataCriacao().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        }

        return "Relatorio-Psicologico-" + data + "-" + normalizarNomeArquivo(nomePaciente) + ".pdf";
    }

    private String normalizarNomeArquivo(String valor) {
        return valor
                .trim()
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("\\s+", "_");
    }

    private String valor(String valor) {
        if (valor == null) {
            return "";
        }

        return valor;
    }

    private String valor(Integer valor) {
        if (valor == null) {
            return "";
        }

        return String.valueOf(valor);
    }
}