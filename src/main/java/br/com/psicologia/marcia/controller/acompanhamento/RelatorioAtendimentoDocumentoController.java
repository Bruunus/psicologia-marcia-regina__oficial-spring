
package br.com.psicologia.marcia.controller.acompanhamento;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.acompanhamento.RelatorioAtendimentoDownloadRequest;
import br.com.psicologia.marcia.service.acompanhamento.RelatorioAtendimentoWordService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/relatorios-atendimento")
@Validated
public class RelatorioAtendimentoDocumentoController {

    private final RelatorioAtendimentoWordService relatorioAtendimentoWordService;

    public RelatorioAtendimentoDocumentoController(
            RelatorioAtendimentoWordService relatorioAtendimentoWordService
    ) {
        this.relatorioAtendimentoWordService = relatorioAtendimentoWordService;
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> download(
            @Valid @RequestBody RelatorioAtendimentoDownloadRequest request
    ) {
        byte[] documento = relatorioAtendimentoWordService.gerarWord(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                ))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(
                                        "Relatorio-Atendimento-"
                                                + request.acompanhamentoId()
                                                + ".docx"
                                )
                                .build()
                                .toString()
                )
                .body(documento);
    }
}
