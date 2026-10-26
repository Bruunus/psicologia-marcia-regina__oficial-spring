
package br.com.psicologia.marcia.controller.queixa;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.queixa.QueixaDownloadRequest;
import br.com.psicologia.marcia.service.queixa.QueixaWordService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/queixas")
@Validated
public class QueixaDocumentoController {

    private final QueixaWordService queixaWordService;

    public QueixaDocumentoController(
            QueixaWordService queixaWordService
    ) {
        this.queixaWordService = queixaWordService;
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> download(
            @Valid @RequestBody QueixaDownloadRequest request
    ) {
        byte[] documento = queixaWordService.gerarWord(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                ))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("Queixa-" + request.queixaId() + ".docx")
                                .build()
                                .toString()
                )
                .body(documento);
    }
}
