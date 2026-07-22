package br.com.psicologia.marcia.controller.relatorio.psicologico;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import br.com.psicologia.marcia.service.relatorio.psicologico.DocumentoRelatorioPsicologicoService;
import br.com.psicologia.marcia.service.relatorio.psicologico.PdfRelatorioPsicologicoService;
import br.com.psicologia.marcia.service.relatorio.psicologico.RelatorioPsicologicoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/relatorios-psicologicos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class RelatorioPsicologicoController {

    @Autowired
    private RelatorioPsicologicoService relatorioPsicologicoService;

    @Autowired
    private DocumentoRelatorioPsicologicoService documentoRelatorioPsicologicoService;

    @Autowired
    private PdfRelatorioPsicologicoService pdfRelatorioPsicologicoService;

    @PostMapping("/salvar")
    public ResponseEntity<?> salvar(@Valid @RequestBody RelatorioPsicologicoRequest request) {
        try {
            RelatorioPsicologicoResponse response = relatorioPsicologicoService.salvar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erro interno ao salvar relatório psicológico"));
        }
    }

    @PostMapping("/listar-por-paciente")
    public ResponseEntity<?> listarPorPaciente(@Valid @RequestBody RelatorioPsicologicoPacienteRequest request) {
        try {
            List<RelatorioPsicologicoResponse> relatorios = relatorioPsicologicoService.listarPorPaciente(request.pacienteId());
            return ResponseEntity.ok(relatorios);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erro interno ao listar relatórios psicológicos"));
        }
    }

    @PostMapping("/buscar")
    public ResponseEntity<?> buscarPorId(@Valid @RequestBody RelatorioPsicologicoBuscarRequest request) {
        try {
            RelatorioPsicologicoResponse relatorio = relatorioPsicologicoService.buscarPorId(request.relatorioId());
            return ResponseEntity.ok(relatorio);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erro interno ao buscar relatório psicológico"));
        }
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> download(@Valid @RequestBody RelatorioPsicologicoDownloadRequest request) {
        DocumentoRelatorioPsicologicoGeradoDTO documentoGerado = documentoRelatorioPsicologicoService.gerarDocumento(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(documentoGerado.getNomeArquivo()).build().toString()
                )
                .body(documentoGerado.getDocumento());
    }

    @PostMapping("/visualizar")
    public ResponseEntity<byte[]> visualizar(@Valid @RequestBody RelatorioPsicologicoVisualizarRequest request) {
        PdfRelatorioPsicologicoGeradoDTO pdfGerado = pdfRelatorioPsicologicoService.gerarPdf(request);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(pdfGerado.getNomeArquivo()).build().toString()
                )
                .body(pdfGerado.getDocumento());
    }
}