
package br.com.psicologia.marcia.controller.arquivo;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.arquivo.ArquivoPacienteDocumentosResponse;
import br.com.psicologia.marcia.DTO.arquivo.BuscarPacienteArquivadoRequest;
import br.com.psicologia.marcia.DTO.arquivo.CarregarArquivoPacienteRequest;
import br.com.psicologia.marcia.DTO.arquivo.PacienteArquivadoResponse;
import br.com.psicologia.marcia.DTO.arquivo.RestaurarTratamentoRequest;

import br.com.psicologia.marcia.service.arquivo.ArquivoPacienteService;

@RestController
@RequestMapping("/api/arquivo")
public class ArquivoPacienteController {

    private final ArquivoPacienteService arquivoPacienteService;

    public ArquivoPacienteController(
            ArquivoPacienteService arquivoPacienteService
    ) {
        this.arquivoPacienteService = arquivoPacienteService;
    }

    @PostMapping("/buscar-paciente")
    public ResponseEntity<?> buscarPacienteArquivado(
            @RequestBody BuscarPacienteArquivadoRequest request
    ) {
        try {
            List<PacienteArquivadoResponse> response =
                    arquivoPacienteService.buscarPacienteArquivado(
                            request
                    );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Collections.singletonMap(
                                    "mensagem",
                                    e.getMessage()
                            )
                    );
        }
    }

    @PostMapping("/carregar-documentos")
    public ResponseEntity<?> carregarDocumentos(
            @RequestBody CarregarArquivoPacienteRequest request
    ) {
        try {
            ArquivoPacienteDocumentosResponse response =
                    arquivoPacienteService.carregarDocumentos(
                            request
                    );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Collections.singletonMap(
                                    "mensagem",
                                    e.getMessage()
                            )
                    );

        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Collections.singletonMap(
                                    "mensagem",
                                    e.getMessage()
                            )
                    );
        }
    }

    @PostMapping("/restaurar-tratamento")
    public ResponseEntity<?> restaurarTratamento(
            @RequestBody RestaurarTratamentoRequest request
    ) {
        try {
            arquivoPacienteService.restaurarTratamento(request);

            return ResponseEntity.ok(
                    Collections.singletonMap(
                            "mensagem",
                            "Tratamento restaurado com sucesso."
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Collections.singletonMap(
                                    "mensagem",
                                    e.getMessage()
                            )
                    );

        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Collections.singletonMap(
                                    "mensagem",
                                    e.getMessage()
                            )
                    );

        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Collections.singletonMap(
                                    "mensagem",
                                    e.getMessage()
                            )
                    );
        }
    }
}
