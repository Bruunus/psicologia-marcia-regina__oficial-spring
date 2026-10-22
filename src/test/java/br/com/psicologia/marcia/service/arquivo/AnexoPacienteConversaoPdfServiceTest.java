
package br.com.psicologia.marcia.service.arquivo;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;

class AnexoPacienteConversaoPdfServiceTest {

    @Test
    void deveConverterDocxParaPdf() throws Exception {

        // Cria um DOCX real em memória.
        byte[] conteudoDocx;

        try (
            XWPFDocument documento = new XWPFDocument();
            ByteArrayOutputStream saida =
                new ByteArrayOutputStream()
        ) {

            documento.createParagraph()
                .createRun()
                .setText(
                    "Teste de conversão DOCX para PDF - Psicologia Márcia"
                );

            documento.write(saida);
            conteudoDocx = saida.toByteArray();
        }

        // Executa a Service implementada.
        AnexoPacienteConversaoPdfService service =
            new AnexoPacienteConversaoPdfService();

        byte[] conteudoPdf =
            service.converterDocxParaPdf(conteudoDocx);

        // Valida o resultado.
        assertNotNull(conteudoPdf);
        assertTrue(conteudoPdf.length > 5);

        String assinatura =
            new String(
                conteudoPdf,
                0,
                5,
                java.nio.charset.StandardCharsets.US_ASCII
            );

        assertEquals("%PDF-", assinatura);
    }
}
