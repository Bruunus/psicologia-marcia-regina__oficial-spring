package br.com.psicologia.marcia.controller.relatorio.psicologico;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.relatorio.psicologico.DocumentoRelatorioPsicologicoGeradoDTO;
import br.com.psicologia.marcia.DTO.relatorio.psicologico.PdfRelatorioPsicologicoGeradoDTO;
import br.com.psicologia.marcia.DTO.relatorio.psicologico.RelatorioPsicologicoBuscarRequest;
import br.com.psicologia.marcia.DTO.relatorio.psicologico.RelatorioPsicologicoDownloadRequest;
import br.com.psicologia.marcia.DTO.relatorio.psicologico.RelatorioPsicologicoPacienteRequest;
import br.com.psicologia.marcia.DTO.relatorio.psicologico.RelatorioPsicologicoRequest;
import br.com.psicologia.marcia.DTO.relatorio.psicologico.RelatorioPsicologicoResponse;
import br.com.psicologia.marcia.DTO.relatorio.psicologico.RelatorioPsicologicoVisualizarRequest;

import br.com.psicologia.marcia.error.ApiErrorResponse;
import br.com.psicologia.marcia.error.ApiNegocioException;

import br.com.psicologia.marcia.service.relatorio.psicologico.DocumentoRelatorioPsicologicoService;
import br.com.psicologia.marcia.service.relatorio.psicologico.PdfRelatorioPsicologicoService;
import br.com.psicologia.marcia.service.relatorio.psicologico.RelatorioPsicologicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/relatorios-psicologicos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class RelatorioPsicologicoController {

    private static final Logger logger =
            LoggerFactory.getLogger(RelatorioPsicologicoController.class);

    private final RelatorioPsicologicoService relatorioPsicologicoService;
    private final DocumentoRelatorioPsicologicoService documentoRelatorioPsicologicoService;
    private final PdfRelatorioPsicologicoService pdfRelatorioPsicologicoService;

    public RelatorioPsicologicoController(
            RelatorioPsicologicoService relatorioPsicologicoService,
            DocumentoRelatorioPsicologicoService documentoRelatorioPsicologicoService,
            PdfRelatorioPsicologicoService pdfRelatorioPsicologicoService
    ) {
        this.relatorioPsicologicoService = relatorioPsicologicoService;
        this.documentoRelatorioPsicologicoService =
                documentoRelatorioPsicologicoService;
        this.pdfRelatorioPsicologicoService =
                pdfRelatorioPsicologicoService;
    }

    @PostMapping("/salvar")
    public ResponseEntity<?> salvar(
            @Valid @RequestBody RelatorioPsicologicoRequest request
    ) {
        try {
            RelatorioPsicologicoResponse response =
                    relatorioPsicologicoService.salvar(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (ApiNegocioException
                 | AccessDeniedException
                 | AuthenticationException e) {

            // Preserva o status definido pela regra de negócio
            // e os mecanismos de segurança existentes.
            throw e;

        } catch (Exception e) {

            logger.error("Falha ao salvar relatório psicológico.", e);

            return erroInterno(
                    "erro_salvar_relatorio_psicologico",
                    "Não foi possível salvar o relatório psicológico."
            );
        }
    }

    @PostMapping("/listar-por-paciente")
    public ResponseEntity<?> listarPorPaciente(
            @Valid @RequestBody RelatorioPsicologicoPacienteRequest request
    ) {
        try {
            List<RelatorioPsicologicoResponse> relatorios =
                    relatorioPsicologicoService.listarPorPaciente(
                            request.pacienteId()
                    );

            return ResponseEntity.ok(relatorios);

        } catch (ApiNegocioException
                 | AccessDeniedException
                 | AuthenticationException e) {

            throw e;

        } catch (Exception e) {

            logger.error("Falha ao listar relatórios psicológicos.", e);

            return erroInterno(
                    "erro_listar_relatorios_psicologicos",
                    "Não foi possível carregar os relatórios psicológicos."
            );
        }
    }

    @PostMapping("/buscar")
    public ResponseEntity<?> buscarPorId(
            @Valid @RequestBody RelatorioPsicologicoBuscarRequest request
    ) {
        try {
            RelatorioPsicologicoResponse relatorio =
                    relatorioPsicologicoService.buscarPorId(
                            request.relatorioId()
                    );

            return ResponseEntity.ok(relatorio);

        } catch (ApiNegocioException
                 | AccessDeniedException
                 | AuthenticationException e) {

            throw e;

        } catch (Exception e) {

            logger.error("Falha ao buscar relatório psicológico.", e);

            return erroInterno(
                    "erro_buscar_relatorio_psicologico",
                    "Não foi possível carregar o relatório psicológico."
            );
        }
    }

    @PostMapping("/download")
    public ResponseEntity<?> download(
            @Valid @RequestBody RelatorioPsicologicoDownloadRequest request
    ) {
        try {
            DocumentoRelatorioPsicologicoGeradoDTO documentoGerado =
                    documentoRelatorioPsicologicoService.gerarDocumento(
                            request
                    );

            return ResponseEntity.ok()
                    .contentType(
                            MediaType.parseMediaType(
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                            )
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename(documentoGerado.getNomeArquivo())
                                    .build()
                                    .toString()
                    )
                    .body(documentoGerado.getDocumento());

        } catch (ApiNegocioException
                 | AccessDeniedException
                 | AuthenticationException e) {

            throw e;

        } catch (Exception e) {

            logger.error(
                    "Falha ao gerar documento Word do relatório psicológico.",
                    e
            );

            return erroInterno(
                    "erro_download_relatorio_psicologico",
                    "Não foi possível gerar o documento Word."
            );
        }
    }

    @PostMapping("/visualizar")
    public ResponseEntity<?> visualizar(
            @Valid @RequestBody RelatorioPsicologicoVisualizarRequest request
    ) {
        try {
            PdfRelatorioPsicologicoGeradoDTO pdfGerado =
                    pdfRelatorioPsicologicoService.gerarPdf(request);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.inline()
                                    .filename(pdfGerado.getNomeArquivo())
                                    .build()
                                    .toString()
                    )
                    .body(pdfGerado.getDocumento());

        } catch (ApiNegocioException
                 | AccessDeniedException
                 | AuthenticationException e) {

            throw e;

        } catch (Exception e) {

            logger.error(
                    "Falha ao visualizar PDF do relatório psicológico.",
                    e
            );

            return erroInterno(
                    "erro_visualizar_relatorio_psicologico",
                    "Não foi possível visualizar o relatório psicológico."
            );
        }
    }

    private ResponseEntity<ApiErrorResponse> erroInterno(
            String codigo,
            String mensagem
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        ApiErrorResponse.criar(
                                status,
                                codigo,
                                mensagem
                        )
                );
    }
}
