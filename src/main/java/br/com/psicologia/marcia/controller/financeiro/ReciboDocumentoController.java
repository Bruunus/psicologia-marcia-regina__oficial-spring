
package br.com.psicologia.marcia.controller.financeiro;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.financeiro.ReciboDownloadRequest;
import br.com.psicologia.marcia.service.financeiro.ReciboWordService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recibos")
@Validated
public class ReciboDocumentoController {

    private final ReciboWordService reciboWordService;

    public ReciboDocumentoController(
            ReciboWordService reciboWordService
    ) {
        this.reciboWordService = reciboWordService;
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> download(
            @Valid @RequestBody ReciboDownloadRequest request
    ) {
        byte[] documento = reciboWordService.gerarWord(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                ))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(
                                        "Recibo-Pagamento-"
                                                + request.pagamentoId()
                                                + ".docx"
                                )
                                .build()
                                .toString()
                )
                .body(documento);
    }
}
