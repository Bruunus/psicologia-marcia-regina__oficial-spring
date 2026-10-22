
package br.com.psicologia.marcia.controller.arquivo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.service.arquivo.AnexoPacienteService;
import br.com.psicologia.marcia.service.arquivo.AnexoPacienteService.AnexoRecuperado;
import br.com.psicologia.marcia.service.arquivo.AnexoPacienteConversaoPdfService;

@RestController
@RequestMapping("/api/arquivo")
public class AnexoPacienteVisualizacaoController {

    private static final long TAMANHO_MAXIMO_DOCX =
            10L * 1024L * 1024L;

    private final AnexoPacienteService anexoPacienteService;

    private final AnexoPacienteConversaoPdfService conversaoPdfService;

    public AnexoPacienteVisualizacaoController(
            AnexoPacienteService anexoPacienteService,
            AnexoPacienteConversaoPdfService conversaoPdfService
    ) {
        this.anexoPacienteService = anexoPacienteService;
        this.conversaoPdfService = conversaoPdfService;
    }

    /*
     * TASK 2.8.6.3
     *
     * PRE-VISUALIZAR ANEXO DOCX COMO PDF
     *
     * POST /api/arquivo/visualizar-anexo
     *
     * O arquivo DOCX original nunca e alterado.
     * A conversao e utilizada exclusivamente
     * para a visualizacao pelo PDF.js.
     */
    @PostMapping(
            value = "/visualizar-anexo",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {
                    MediaType.APPLICATION_PDF_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public ResponseEntity<?> visualizarAnexo(
            @RequestBody VisualizarAnexoRequest request
    ) {

        try {

            Long pacienteId = request == null
                    ? null
                    : request.pacienteId();

            Long anexoId = request == null
                    ? null
                    : request.anexoId();

            /*
             * Reutiliza integralmente as validacoes
             * existentes:
             *
             * - Paciente arquivado
             * - Anexo pertencente ao paciente
             * - Arquivo existente no armazenamento
             */
            AnexoRecuperado anexo =
                    anexoPacienteService.recuperarAnexo(
                            pacienteId,
                            anexoId
                    );

            String mimeDocx =
                    "application/vnd.openxmlformats-officedocument"
                    + ".wordprocessingml.document";

            if (!mimeDocx.equalsIgnoreCase(anexo.tipoMime())) {
                throw new IllegalArgumentException(
                        "Este endpoint aceita apenas anexos DOCX."
                );
            }

            if (
                    anexo.tamanhoBytes() == null
                    || anexo.tamanhoBytes() <= 0
                    || anexo.tamanhoBytes() > TAMANHO_MAXIMO_DOCX
            ) {
                throw new IllegalArgumentException(
                        "O tamanho do DOCX é inválido."
                );
            }

            /*
             * Limita a leitura em memoria mesmo que
             * o arquivo fisico esteja maior que os
             * metadados registrados no banco.
             */
            byte[] conteudoDocx;

            try (InputStream entrada =
                         anexo.recurso().getInputStream()) {

                conteudoDocx = entrada.readNBytes(
                        (int) TAMANHO_MAXIMO_DOCX + 1
                );
            }

            if (
                    conteudoDocx.length == 0
                    || conteudoDocx.length > TAMANHO_MAXIMO_DOCX
            ) {
                throw new IllegalArgumentException(
                        "O conteúdo DOCX possui tamanho inválido."
                );
            }

            /*
             * Executa a conversao temporaria.
             * O original permanece no armazenamento.
             */
            byte[] conteudoPdf =
                    conversaoPdfService.converterDocxParaPdf(
                            conteudoDocx
                    );

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(conteudoPdf.length)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline"
                    )
                    .header(
                            "X-Content-Type-Options",
                            "nosniff"
                    )
                    .cacheControl(CacheControl.noStore())
                    .body(conteudoPdf);

        } catch (IllegalArgumentException e) {

            return respostaErro(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );

        } catch (NoSuchElementException e) {

            return respostaErro(
                    HttpStatus.NOT_FOUND,
                    e.getMessage()
            );

        } catch (IOException | IllegalStateException e) {

            return respostaErro(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível gerar a pré-visualização do DOCX."
            );
        }
    }

    private ResponseEntity<?> respostaErro(
            HttpStatus status,
            String mensagem
    ) {
        return ResponseEntity.status(status)
                .cacheControl(CacheControl.noStore())
                .body(
                        Collections.singletonMap(
                                "mensagem",
                                mensagem
                        )
                );
    }

    public record VisualizarAnexoRequest(
            Long pacienteId,
            Long anexoId
    ) {
    }
}
