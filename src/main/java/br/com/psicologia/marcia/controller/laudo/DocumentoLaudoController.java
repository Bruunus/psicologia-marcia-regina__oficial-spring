package br.com.psicologia.marcia.controller.laudo;

import java.text.Normalizer;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.laudo.DocumentoLaudoGeradoDTO;
import br.com.psicologia.marcia.DTO.laudo.LaudoDownloadRequest;
import br.com.psicologia.marcia.DTO.laudo.LaudoResponse;
import br.com.psicologia.marcia.DTO.laudo.LaudoSalvarRequest;
import br.com.psicologia.marcia.service.laudo.DocumentoLaudoService;
import br.com.psicologia.marcia.service.laudo.LaudoService;

@RestController
@RequestMapping("/api/laudos")
public class DocumentoLaudoController {

    private static final String DOCX_CONTENT_TYPE =
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    private final LaudoService laudoService;
    private final DocumentoLaudoService documentoLaudoService;

    public DocumentoLaudoController(
        LaudoService laudoService,
        DocumentoLaudoService documentoLaudoService
    ) {
        this.laudoService = laudoService;
        this.documentoLaudoService = documentoLaudoService;
    }

    @PostMapping("/salvar")
    public ResponseEntity<LaudoResponse> salvar(
        @RequestBody LaudoSalvarRequest request
    ) {
        LaudoResponse response =
            laudoService.salvar(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @PostMapping(
        value = "/gerar-docx",
        produces = DOCX_CONTENT_TYPE
    )
    public ResponseEntity<byte[]> gerarDocx(
        @RequestBody LaudoDownloadRequest request
    ) {
        DocumentoLaudoGeradoDTO documentoGerado =
            documentoLaudoService.gerarDocumento(request);

        String nomeArquivoSeguro =
            normalizarNomeArquivo(
                documentoGerado.nomeArquivo()
            );

        HttpHeaders headers =
            new HttpHeaders();

        headers.setContentType(
            MediaType.parseMediaType(
                DOCX_CONTENT_TYPE
            )
        );

        headers.set(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\""
                + nomeArquivoSeguro
                + "\""
        );

        headers.set(
            HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
            HttpHeaders.CONTENT_DISPOSITION
        );

        headers.set(
            "X-Content-Type-Options",
            "nosniff"
        );

        headers.setCacheControl(
            CacheControl
                .noStore()
                .mustRevalidate()
        );

        headers.setPragma("no-cache");

        headers.setContentLength(
            documentoGerado.arquivo().length
        );

        return new ResponseEntity<>(
            documentoGerado.arquivo(),
            headers,
            HttpStatus.OK
        );
    }

    private String normalizarNomeArquivo(
        String nomeArquivo
    ) {
        if (
            nomeArquivo == null ||
            nomeArquivo.isBlank()
        ) {
            return "Laudo-Neuropsicologico.docx";
        }

        String nomeSemAcentos =
            Normalizer
                .normalize(
                    nomeArquivo,
                    Normalizer.Form.NFD
                )
                .replaceAll(
                    "\\p{M}",
                    ""
                );

        String nomeSeguro =
            nomeSemAcentos
                .replaceAll(
                    "[\\\\/:*?\"<>|]",
                    ""
                )
                .replaceAll(
                    "\\s+",
                    "_"
                )
                .replaceAll(
                    "[^a-zA-Z0-9._-]",
                    ""
                );

        while (
            nomeSeguro
                .toLowerCase()
                .endsWith(".txt")
        ) {
            nomeSeguro =
                nomeSeguro.substring(
                    0,
                    nomeSeguro.length() - 4
                );
        }

        if (
            !nomeSeguro
                .toLowerCase()
                .endsWith(".docx")
        ) {
            nomeSeguro += ".docx";
        }

        return nomeSeguro;
    }
}