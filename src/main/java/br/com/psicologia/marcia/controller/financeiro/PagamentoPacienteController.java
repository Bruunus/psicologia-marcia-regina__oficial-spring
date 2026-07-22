package br.com.psicologia.marcia.controller.financeiro;

import br.com.psicologia.marcia.DTO.financeiro.BuscarPagamentoPacienteRequest;
import br.com.psicologia.marcia.DTO.financeiro.ConfirmarPagamentoRequest;
import br.com.psicologia.marcia.DTO.financeiro.DeletarPagamentoPacienteRequest;
import br.com.psicologia.marcia.DTO.financeiro.EditarPagamentoPacienteRequest;
import br.com.psicologia.marcia.DTO.financeiro.ListarPagamentoPacienteRequest;
import br.com.psicologia.marcia.DTO.financeiro.PagamentoPacienteResponse;
import br.com.psicologia.marcia.DTO.financeiro.SalvarPagamentoPacienteRequest;
import br.com.psicologia.marcia.service.financeiro.PagamentoPacienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/financeiro-paciente")
public class PagamentoPacienteController {

    private final PagamentoPacienteService pagamentoPacienteService;

    public PagamentoPacienteController(PagamentoPacienteService pagamentoPacienteService) {
        this.pagamentoPacienteService = pagamentoPacienteService;
    }

    @PostMapping("/salvar")
    public ResponseEntity<PagamentoPacienteResponse> salvar(
            @RequestBody SalvarPagamentoPacienteRequest request
    ) {
        PagamentoPacienteResponse response = pagamentoPacienteService.salvar(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/listar-por-paciente")
    public ResponseEntity<List<PagamentoPacienteResponse>> listarPorPaciente(
            @RequestBody ListarPagamentoPacienteRequest request
    ) {
        List<PagamentoPacienteResponse> response = pagamentoPacienteService.listarPorPaciente(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/buscar-por-id")
    public ResponseEntity<PagamentoPacienteResponse> buscarPorId(
            @RequestBody BuscarPagamentoPacienteRequest request
    ) {
        PagamentoPacienteResponse response = pagamentoPacienteService.buscarPorId(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/editar")
    public ResponseEntity<PagamentoPacienteResponse> editar(
            @RequestBody EditarPagamentoPacienteRequest request
    ) {
        PagamentoPacienteResponse response = pagamentoPacienteService.editar(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirmar-pagamento")
    public ResponseEntity<PagamentoPacienteResponse> confirmarPagamento(
            @RequestBody ConfirmarPagamentoRequest request
    ) {
        PagamentoPacienteResponse response = pagamentoPacienteService.confirmarPagamento(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deletar")
    public ResponseEntity<Void> deletar(
            @RequestBody DeletarPagamentoPacienteRequest request
    ) {
        pagamentoPacienteService.deletar(request);
        return ResponseEntity.noContent().build();
    }
}