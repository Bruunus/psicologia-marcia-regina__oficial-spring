package br.com.psicologia.marcia.controller.acompanhamento;

import java.net.URI;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.acompanhamento.AcompanhamentoPacienteResponseDTO;
import br.com.psicologia.marcia.DTO.acompanhamento.CriarAcompanhamentoPacienteDTO;
import br.com.psicologia.marcia.DTO.acompanhamento.DocumentoHistoricoAtendimentoGeradoDTO;
import br.com.psicologia.marcia.DTO.acompanhamento.EmitirHistoricoAtendimentoRequestDTO;
import br.com.psicologia.marcia.service.acompanhamento.AcompanhamentoPacienteService;
import br.com.psicologia.marcia.service.acompanhamento.DocumentoHistoricoAtendimentoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/acompanhamentos")
public class AcompanhamentoPacienteController {

    private final AcompanhamentoPacienteService acompanhamentoPacienteService;
    private final DocumentoHistoricoAtendimentoService documentoHistoricoAtendimentoService;

    public AcompanhamentoPacienteController(
            AcompanhamentoPacienteService acompanhamentoPacienteService,
            DocumentoHistoricoAtendimentoService documentoHistoricoAtendimentoService
    ) {
        this.acompanhamentoPacienteService = acompanhamentoPacienteService;
        this.documentoHistoricoAtendimentoService = documentoHistoricoAtendimentoService;
    }

    @PostMapping("/paciente/{pacienteId}")
    public ResponseEntity<AcompanhamentoPacienteResponseDTO> criar(
            @PathVariable Long pacienteId,
            @Valid @RequestBody CriarAcompanhamentoPacienteDTO dto
    ) {
        AcompanhamentoPacienteResponseDTO acompanhamentoCriado =
                acompanhamentoPacienteService.criar(pacienteId, dto);

        URI uri = URI.create("/api/acompanhamentos/" + acompanhamentoCriado.getId());

        return ResponseEntity.created(uri).body(acompanhamentoCriado);
    }

    @GetMapping
    public ResponseEntity<List<AcompanhamentoPacienteResponseDTO>> listarTodos() {
        List<AcompanhamentoPacienteResponseDTO> acompanhamentos =
                acompanhamentoPacienteService.listarTodos();

        return ResponseEntity.ok(acompanhamentos);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<AcompanhamentoPacienteResponseDTO>> listarPorPaciente(
            @PathVariable Long pacienteId
    ) {
        List<AcompanhamentoPacienteResponseDTO> acompanhamentos =
                acompanhamentoPacienteService.listarPorPaciente(pacienteId);

        return ResponseEntity.ok(acompanhamentos);
    }

    @GetMapping("/{acompanhamentoId}")
    public ResponseEntity<AcompanhamentoPacienteResponseDTO> buscarPorId(
            @PathVariable Long acompanhamentoId
    ) {
        AcompanhamentoPacienteResponseDTO acompanhamento =
                acompanhamentoPacienteService.buscarPorId(acompanhamentoId);

        return ResponseEntity.ok(acompanhamento);
    }

    @PutMapping("/{acompanhamentoId}")
    public ResponseEntity<AcompanhamentoPacienteResponseDTO> atualizar(
            @PathVariable Long acompanhamentoId,
            @Valid @RequestBody CriarAcompanhamentoPacienteDTO dto
    ) {
        AcompanhamentoPacienteResponseDTO acompanhamentoAtualizado =
                acompanhamentoPacienteService.atualizar(acompanhamentoId, dto);

        return ResponseEntity.ok(acompanhamentoAtualizado);
    }

    @DeleteMapping("/{acompanhamentoId}")
    public ResponseEntity<Void> excluirFalso(
            @PathVariable Long acompanhamentoId
    ) {
        acompanhamentoPacienteService.excluirFalso(acompanhamentoId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{acompanhamentoId}/registrar-ausencia")
    public ResponseEntity<AcompanhamentoPacienteResponseDTO> registrarAusencia(
            @PathVariable Long acompanhamentoId
    ) {
        AcompanhamentoPacienteResponseDTO acompanhamentoAtualizado =
                acompanhamentoPacienteService.registrarAusencia(acompanhamentoId);

        return ResponseEntity.ok(acompanhamentoAtualizado);
    }

    @GetMapping("/paciente/{pacienteId}/historico")
    public ResponseEntity<List<AcompanhamentoPacienteResponseDTO>> listarParaHistorico(
            @PathVariable Long pacienteId
    ) {
        List<AcompanhamentoPacienteResponseDTO> acompanhamentos =
                acompanhamentoPacienteService.listarParaHistorico(pacienteId);

        return ResponseEntity.ok(acompanhamentos);
    }

    @PostMapping("/emitir-historico")
    public ResponseEntity<byte[]> emitirHistorico(
            @Valid @RequestBody EmitirHistoricoAtendimentoRequestDTO dto
    ) {
        DocumentoHistoricoAtendimentoGeradoDTO documentoGerado =
                documentoHistoricoAtendimentoService.gerarDocumentoHistorico(dto);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                ))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(documentoGerado.getNomeArquivo())
                                .build()
                                .toString()
                )
                .body(documentoGerado.getDocumento());
    }
    
    
    @PatchMapping("/{acompanhamentoId}/cancelar-ausencia")
    public ResponseEntity<AcompanhamentoPacienteResponseDTO> cancelarAusencia(
            @PathVariable Long acompanhamentoId
    ) {
        AcompanhamentoPacienteResponseDTO response =
                acompanhamentoPacienteService.cancelarAusencia(acompanhamentoId);

        return ResponseEntity.ok(response);
    }
    
    
}