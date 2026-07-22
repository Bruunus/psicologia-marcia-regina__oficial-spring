package br.com.psicologia.marcia.service.documento;

import java.time.format.DateTimeFormatter;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;

import br.com.psicologia.marcia.model.Endereco;
import br.com.psicologia.marcia.model.Paciente;

@Service
public class BlocoIdentificacaoDocumentoService {

    private final DocumentoLayoutConfig documentoLayoutConfig;

    public BlocoIdentificacaoDocumentoService(DocumentoLayoutConfig documentoLayoutConfig) {
        this.documentoLayoutConfig = documentoLayoutConfig;
    }

    /*
     * =========================================================
     * BLOCO DE IDENTIFICAÇÃO - DOCX
     * =========================================================
     */

    public void criarDocx(XWPFDocument documento, Paciente paciente) {
        XWPFParagraph linhaNome = criarParagrafoDocx(documento, ParagraphAlignment.LEFT);

        adicionarTextoNegritoDocx(linhaNome, "Nome: ");
        adicionarTextoNormalDocx(linhaNome, valor(paciente.getNomeCompleto()));

        XWPFParagraph linhaCpfRg = criarParagrafoDocx(documento, ParagraphAlignment.LEFT);

        adicionarTextoNegritoDocx(linhaCpfRg, "CPF: ");
        adicionarTextoNormalDocx(linhaCpfRg, formatarCpf(paciente.getCpf()));
        adicionarTextoNormalDocx(linhaCpfRg, "    ");
        adicionarTextoNegritoDocx(linhaCpfRg, "RG: ");
        adicionarTextoNormalDocx(linhaCpfRg, formatarRg(paciente.getRg()));

        XWPFParagraph linhaNascimentoIdade = criarParagrafoDocx(documento, ParagraphAlignment.LEFT);

        adicionarTextoNegritoDocx(linhaNascimentoIdade, "DN: ");
        adicionarTextoNormalDocx(linhaNascimentoIdade, formatarDataNascimento(paciente));
        adicionarTextoNormalDocx(linhaNascimentoIdade, "    ");
        adicionarTextoNegritoDocx(linhaNascimentoIdade, "Idade: ");
        adicionarTextoNormalDocx(linhaNascimentoIdade, formatarIdade(paciente));

        XWPFParagraph linhaEndereco = criarParagrafoDocx(documento, ParagraphAlignment.LEFT);

        adicionarTextoNegritoDocx(linhaEndereco, "Endereço: ");
        adicionarTextoNormalDocx(linhaEndereco, montarEnderecoPaciente(paciente));

        XWPFParagraph linhaContato = criarParagrafoDocx(documento, ParagraphAlignment.LEFT);

        adicionarTextoNegritoDocx(linhaContato, "Tel. contato: ");
        adicionarTextoNormalDocx(linhaContato, formatarTelefone(paciente.getTelefoneContato()));
    }

    private XWPFParagraph criarParagrafoDocx(XWPFDocument documento, ParagraphAlignment alinhamento) {
        XWPFParagraph paragrafo = documento.createParagraph();

        paragrafo.setAlignment(alinhamento);
        paragrafo.setSpacingAfter(documentoLayoutConfig.espacamentoPadraoDepoisParagrafoTwips());

        return paragrafo;
    }

    private void adicionarTextoNegritoDocx(XWPFParagraph paragrafo, String texto) {
        XWPFRun run = paragrafo.createRun();

        aplicarFontePadraoDocx(run);
        run.setBold(true);
        run.setText(texto);
    }

    private void adicionarTextoNormalDocx(XWPFParagraph paragrafo, String texto) {
        XWPFRun run = paragrafo.createRun();

        aplicarFontePadraoDocx(run);
        run.setText(texto);
    }

    private void aplicarFontePadraoDocx(XWPFRun run) {
        run.setFontFamily(documentoLayoutConfig.fonteTexto());
        run.setFontSize(documentoLayoutConfig.tamanhoFonteTexto());
    }

    /*
     * =========================================================
     * BLOCO DE IDENTIFICAÇÃO - PDF
     * =========================================================
     */

    public void criarPdf(Document documento, Paciente paciente) {
        documento.add(criarLinhaIdentificacaoPdf("Nome: ", valor(paciente.getNomeCompleto())));
        documento.add(criarLinhaDuplaPdf("CPF: ", formatarCpf(paciente.getCpf()), "RG: ", formatarRg(paciente.getRg())));
        documento.add(criarLinhaDuplaPdf("DN: ", formatarDataNascimento(paciente), "Idade: ", formatarIdade(paciente)));
        documento.add(criarLinhaIdentificacaoPdf("Endereço: ", montarEnderecoPaciente(paciente)));
        documento.add(criarLinhaIdentificacaoPdf("Tel. contato: ", formatarTelefone(paciente.getTelefoneContato())));
    }

    private Paragraph criarLinhaIdentificacaoPdf(String rotulo, String valor) {
        Paragraph paragrafo = new Paragraph();

        paragrafo.setAlignment(Element.ALIGN_LEFT);
        paragrafo.setSpacingAfter(documentoLayoutConfig.twipsParaPontos(
                documentoLayoutConfig.espacamentoPadraoDepoisParagrafoTwips()
        ));

        paragrafo.add(new Chunk(rotulo, fonteTextoNegritoPdf()));
        paragrafo.add(new Chunk(valor, fonteTextoPdf()));

        return paragrafo;
    }

    private Paragraph criarLinhaDuplaPdf(String rotuloUm, String valorUm, String rotuloDois, String valorDois) {
        Paragraph paragrafo = new Paragraph();

        paragrafo.setAlignment(Element.ALIGN_LEFT);
        paragrafo.setSpacingAfter(documentoLayoutConfig.twipsParaPontos(
                documentoLayoutConfig.espacamentoPadraoDepoisParagrafoTwips()
        ));

        paragrafo.add(new Chunk(rotuloUm, fonteTextoNegritoPdf()));
        paragrafo.add(new Chunk(valorUm, fonteTextoPdf()));
        paragrafo.add(new Chunk("          ", fonteTextoPdf()));
        paragrafo.add(new Chunk(rotuloDois, fonteTextoNegritoPdf()));
        paragrafo.add(new Chunk(valorDois, fonteTextoPdf()));

        return paragrafo;
    }

    private Font fonteTextoPdf() {
        return FontFactory.getFont(
                documentoLayoutConfig.fonteTextoPdf(),
                documentoLayoutConfig.tamanhoFonteTexto(),
                Font.NORMAL
        );
    }

    private Font fonteTextoNegritoPdf() {
        return FontFactory.getFont(
                documentoLayoutConfig.fonteTextoPdf(),
                documentoLayoutConfig.tamanhoFonteTexto(),
                Font.BOLD
        );
    }

    /*
     * =========================================================
     * FORMATAÇÕES PADRÃO
     * =========================================================
     */

    private String montarEnderecoPaciente(Paciente paciente) {
        if (paciente == null || paciente.getEndereco() == null) {
            return "";
        }

        Endereco endereco = paciente.getEndereco();
        StringBuilder enderecoFormatado = new StringBuilder();

        adicionarParteEndereco(enderecoFormatado, endereco.getLogradouro());
        adicionarParteEndereco(enderecoFormatado, endereco.getNumero());
        adicionarParteEndereco(enderecoFormatado, endereco.getBairro());
        adicionarParteEndereco(enderecoFormatado, endereco.getCidade());
        adicionarParteEndereco(enderecoFormatado, endereco.getUf());

        if (endereco.getCep() != null && !endereco.getCep().isBlank()) {
            if (!enderecoFormatado.isEmpty()) {
                enderecoFormatado.append(" - ");
            }

            enderecoFormatado.append(formatarCep(endereco.getCep()));
        }

        return enderecoFormatado.toString();
    }

    private void adicionarParteEndereco(StringBuilder endereco, String parte) {
        if (parte == null || parte.isBlank()) {
            return;
        }

        if (!endereco.isEmpty()) {
            endereco.append(", ");
        }

        endereco.append(parte.trim());
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return "";
        }

        String numeros = cpf.replaceAll("\\D", "");

        if (numeros.length() != 11) {
            return cpf;
        }

        return numeros.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    private String formatarRg(String rg) {
        if (rg == null || rg.isBlank()) {
            return "";
        }

        String numeros = rg.replaceAll("\\D", "");

        if (numeros.length() == 9) {
            return numeros.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{1})", "$1.$2.$3-$4");
        }

        return rg;
    }

    private String formatarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return "";
        }

        String numeros = telefone.replaceAll("\\D", "");

        if (numeros.length() == 11) {
            return numeros.replaceFirst("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }

        if (numeros.length() == 10) {
            return numeros.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }

        return telefone;
    }

    private String formatarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            return "";
        }

        String numeros = cep.replaceAll("\\D", "");

        if (numeros.length() != 8) {
            return cep;
        }

        return numeros.replaceFirst("(\\d{5})(\\d{3})", "$1-$2");
    }

    private String formatarDataNascimento(Paciente paciente) {
        if (paciente == null || paciente.getDataNascimento() == null) {
            return "";
        }

        return paciente.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String formatarIdade(Paciente paciente) {
        if (paciente == null || paciente.getIdade() == null) {
            return "";
        }

        return paciente.getIdade() + " anos";
    }

    private String valor(String valor) {
        if (valor == null) {
            return "";
        }

        return valor;
    }
}