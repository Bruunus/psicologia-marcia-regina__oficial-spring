package br.com.psicologia.marcia.controller.faturamento;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.faturamento.ListarMeuFaturamentoRequest;
import br.com.psicologia.marcia.DTO.faturamento.MeuFaturamentoResponse;
import br.com.psicologia.marcia.service.faturamento.MeuFaturamentoService;

@RestController
@RequestMapping("/api/meu-faturamento")
public class MeuFaturamentoController {

    private final MeuFaturamentoService meuFaturamentoService;

    public MeuFaturamentoController(
            MeuFaturamentoService meuFaturamentoService
    ) {
        this.meuFaturamentoService = meuFaturamentoService;
    }

    @PostMapping("/listar")
    public ResponseEntity<MeuFaturamentoResponse> listar(
            @RequestBody(required = false)
            ListarMeuFaturamentoRequest request
    ) {
        MeuFaturamentoResponse response =
                meuFaturamentoService.listar(request);

        return ResponseEntity.ok(response);
    }
}