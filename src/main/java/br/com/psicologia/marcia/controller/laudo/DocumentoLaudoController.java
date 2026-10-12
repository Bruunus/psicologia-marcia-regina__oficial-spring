package br.com.psicologia.marcia.controller.laudo;

import java.text.Normalizer;
import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
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
import br.com.psicologia.marcia.DTO.laudo.LaudoExcluirRequest;
import br.com.psicologia.marcia.DTO.laudo.LaudoListaResponse;
import br.com.psicologia.marcia.DTO.laudo.LaudoPacienteRequest;
import br.com.psicologia.marcia.DTO.laudo.LaudoResponse;
import br.com.psicologia.marcia.DTO.laudo.LaudoSalvarRequest;
import br.com.psicologia.marcia.DTO.laudo.LaudoVisualizarRequest;
import br.com.psicologia.marcia.DTO.laudo.PdfLaudoGeradoDTO;
import br.com.psicologia.marcia.service.laudo.DocumentoLaudoService;
import br.com.psicologia.marcia.service.laudo.LaudoService;
import br.com.psicologia.marcia.service.laudo.PdfLaudoService;


@RestController
@RequestMapping("/api/laudos")
public class DocumentoLaudoController {

    private static final String DOCX_CONTENT_TYPE =
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document";


    private final LaudoService laudoService;

    private final DocumentoLaudoService documentoLaudoService;

    private final PdfLaudoService pdfLaudoService;


    public DocumentoLaudoController(
        LaudoService laudoService,
        DocumentoLaudoService documentoLaudoService,
        PdfLaudoService pdfLaudoService
    ) {

        this.laudoService =
            laudoService;

        this.documentoLaudoService =
            documentoLaudoService;

        this.pdfLaudoService =
            pdfLaudoService;
    }


    /*
     * ============================================================
     * SALVAR
     * ============================================================
     */

    @PostMapping("/salvar")
    public ResponseEntity<LaudoResponse> salvar(
        @RequestBody LaudoSalvarRequest request
    ) {

        LaudoResponse response =
            laudoService.salvar(
                request
            );


        return ResponseEntity
            .status(
                HttpStatus.CREATED
            )
            .body(
                response
            );
    }


    /*
     * ============================================================
     * LISTAR POR PACIENTE
     * ============================================================
     */

    @PostMapping("/listar-por-paciente")
    public ResponseEntity<List<LaudoListaResponse>> listarPorPaciente(
        @RequestBody LaudoPacienteRequest request
    ) {

        List<LaudoListaResponse> laudos =
            laudoService.listarPorPaciente(
                request.pacienteId()
            );


        return ResponseEntity.ok(
            laudos
        );
    }


    /*
     * ============================================================
     * EXCLUSÃO LÓGICA
     * ============================================================
     */

    @PostMapping("/excluir")
    public ResponseEntity<Void> excluir(
        @RequestBody LaudoExcluirRequest request
    ) {

        laudoService.excluir(
            request.laudoId()
        );


        return ResponseEntity
            .noContent()
            .build();
    }


    /*
     * ============================================================
     * VISUALIZAR PDF
     * ============================================================
     */

    @PostMapping(
        value = "/visualizar",
        produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> visualizar(
        @RequestBody LaudoVisualizarRequest request
    ) {

        PdfLaudoGeradoDTO pdfGerado =
            pdfLaudoService.gerarPdf(
                request
            );


        return ResponseEntity
            .ok()
            .contentType(
                MediaType.APPLICATION_PDF
            )
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition
                    .inline()
                    .filename(
                        pdfGerado.nomeArquivo()
                    )
                    .build()
                    .toString()
            )
            .body(
                pdfGerado.documento()
            );
    }


    /*
     * ============================================================
     * GERAR WORD
     * ============================================================
     */

    @PostMapping(
        value = "/gerar-docx",
        produces = DOCX_CONTENT_TYPE
    )
    public ResponseEntity<byte[]> gerarDocx(
        @RequestBody LaudoDownloadRequest request
    ) {

        DocumentoLaudoGeradoDTO documentoGerado =
            documentoLaudoService
                .gerarDocumento(
                    request
                );


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
            "attachment;filename=\""
                +
                nomeArquivoSeguro
                +
                "\""
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


        headers.setPragma(
            "no-cache"
        );


        headers.setContentLength(
            documentoGerado
                .arquivo()
                .length
        );


        return new ResponseEntity<>(
            documentoGerado.arquivo(),
            headers,
            HttpStatus.OK
        );
    }


    /*
     * ============================================================
     * NORMALIZAÇÃO DO NOME DO ARQUIVO
     * ============================================================
     */

    private String normalizarNomeArquivo(
        String nomeArquivo
    ) {

        if (
            nomeArquivo == null
            ||
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
                .endsWith(
                    ".txt"
                )
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
                .endsWith(
                    ".docx"
                )
        ) {

            nomeSeguro +=
                ".docx";
        }


        return nomeSeguro;
    }
}