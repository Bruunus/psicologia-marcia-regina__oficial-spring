package br.com.psicologia.marcia.controller.finalizacao;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.finalizacao.FinalizarAtendimentoRequest;
import br.com.psicologia.marcia.DTO.finalizacao.FinalizarAtendimentoResponse;
import br.com.psicologia.marcia.service.finalizacao.FinalizarAtendimentoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/paciente")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FinalizarAtendimentoController {

    @Autowired
    private FinalizarAtendimentoService finalizarAtendimentoService;

    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizarAtendimento(
            @Valid
            @RequestBody
            FinalizarAtendimentoRequest request
    ) {

        try {
            FinalizarAtendimentoResponse response =
                    finalizarAtendimentoService.finalizarAtendimento(
                            request.pacienteId()
                    );

            if (response.pagamentosPendentes()) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(response);
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Collections.singletonMap(
                                    "message",
                                    e.getMessage()
                            )
                    );

        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Collections.singletonMap(
                                    "message",
                                    e.getMessage()
                            )
                    );

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Collections.singletonMap(
                                    "message",
                                    "Erro interno ao finalizar o atendimento."
                            )
                    );
        }
    }
}