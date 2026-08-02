package br.com.psicologia.marcia.controller.agenda;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.agenda.AgendamentoCalendarioDiaResponse;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoConsultaRequest;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoDataRequest;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoIdRequest;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoMesRequest;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoOrcamentoRequest;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoPeriodoRequest;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoResponse;
import br.com.psicologia.marcia.DTO.agenda.ConflitoAgendamentoResponse;
import br.com.psicologia.marcia.DTO.agenda.ResultadoAgendamentoConsultaResponse;
import br.com.psicologia.marcia.service.agenda.AgendamentoService;
import br.com.psicologia.marcia.DTO.agenda.AgendamentoLembreteResponse;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService
        agendamentoService;

    public AgendamentoController(
        AgendamentoService agendamentoService
    ) {
        this.agendamentoService =
            agendamentoService;
    }

    @PostMapping("/salvar-orcamento")
    public ResponseEntity<?> salvarOrcamento(
        @RequestBody
        AgendamentoOrcamentoRequest request
    ) {
        try {
            List<AgendamentoResponse> conflitos =
                agendamentoService
                    .buscarConflitos(request);

            boolean confirmarConflito =
                Boolean.TRUE.equals(
                    request.getConfirmarConflito()
                );

            if (
                !conflitos.isEmpty()
                    && !confirmarConflito
            ) {
                ConflitoAgendamentoResponse
                    response =
                        new ConflitoAgendamentoResponse(
                            true,
                            "Já existe um agendamento nesta data e dentro da mesma hora.",
                            conflitos
                        );

                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
            }

            AgendamentoResponse response =
                agendamentoService
                    .salvarOrcamento(request);

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

        } catch (
            IllegalArgumentException exception
        ) {
            return criarRespostaErro(exception);
        }
    }

    @PostMapping("/salvar-consulta")
    public ResponseEntity<?> salvarConsulta(
        @RequestBody
        AgendamentoConsultaRequest request
    ) {
        try {
            ResultadoAgendamentoConsultaResponse
                resultado =
                    agendamentoService
                        .salvarConsulta(request);

            if (
                resultado
                    .isBloqueadoPorConflito()
            ) {
                int quantidadeConflitante =
                    resultado
                        .getDatasConflitantes()
                        .size();

                int quantidadeDisponivel =
                    resultado
                        .getQuantidadeSolicitada()
                        - quantidadeConflitante;

                ConflitoAgendamentoResponse
                    response =
                        new ConflitoAgendamentoResponse(
                            true,
                            resultado.getMensagem(),
                            resultado
                                .getQuantidadeSolicitada(),
                            quantidadeConflitante,
                            quantidadeDisponivel,
                            resultado
                                .getDatasConflitantes(),
                            resultado
                                .getAgendamentosConflitantes()
                        );

                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
            }

            if (
                resultado.getQuantidadeSalva()
                    == 0
            ) {
                return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(resultado);
            }

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resultado);

        } catch (
            IllegalArgumentException exception
        ) {
            return criarRespostaErro(exception);
        }
    }

    @PostMapping("/listar-todos")
    public ResponseEntity<
        List<AgendamentoResponse>
    > listarTodos() {
        List<AgendamentoResponse> response =
            agendamentoService.listarTodos();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/listar-por-data")
    public ResponseEntity<?> listarPorData(
        @RequestBody
        AgendamentoDataRequest request
    ) {
        try {
            List<AgendamentoResponse> response =
                agendamentoService
                    .listarPorData(request);

            return ResponseEntity.ok(response);

        } catch (
            IllegalArgumentException exception
        ) {
            return criarRespostaErro(exception);
        }
    }

    @PostMapping("/listar-por-periodo")
    public ResponseEntity<?> listarPorPeriodo(
        @RequestBody
        AgendamentoPeriodoRequest request
    ) {
        try {
            List<AgendamentoResponse> response =
                agendamentoService
                    .listarPorPeriodo(request);

            return ResponseEntity.ok(response);

        } catch (
            IllegalArgumentException exception
        ) {
            return criarRespostaErro(exception);
        }
    }

    @PostMapping("/calendario-mensal")
    public ResponseEntity<?>
        listarCalendarioMensal(
            @RequestBody
            AgendamentoMesRequest request
        ) {
        try {
            List<AgendamentoCalendarioDiaResponse>
                response =
                    agendamentoService
                        .listarCalendarioMensal(
                            request
                        );

            return ResponseEntity.ok(response);

        } catch (
            IllegalArgumentException exception
        ) {
            return criarRespostaErro(exception);
        }
    }

    @PostMapping("/buscar-por-id")
    public ResponseEntity<?> buscarPorId(
        @RequestBody
        AgendamentoIdRequest request
    ) {
        try {
            AgendamentoResponse response =
                agendamentoService
                    .buscarPorId(request);

            return ResponseEntity.ok(response);

        } catch (
            NoSuchElementException exception
        ) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                    Map.of(
                        "mensagem",
                        exception.getMessage()
                    )
                );

        } catch (
            IllegalArgumentException exception
        ) {
            return criarRespostaErro(exception);
        }
    }
    
    @PostMapping("/excluir")
    public ResponseEntity<?> excluirAgendamento(
        @RequestBody AgendamentoIdRequest request
    ) {
        try {
            agendamentoService.excluirAgendamento(
                request
            );

            return ResponseEntity.ok(
                Map.of(
                    "mensagem",
                    "Agendamento excluído com sucesso."
                )
            );

        } catch (NoSuchElementException exception) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                    Map.of(
                        "mensagem",
                        exception.getMessage()
                    )
                );

        } catch (IllegalArgumentException exception) {
            return criarRespostaErro(exception);
        }
    }
    
    @PostMapping("/lembrete-proximo-dia")
    public ResponseEntity<
        AgendamentoLembreteResponse
    > buscarLembreteProximoDia() {

        AgendamentoLembreteResponse response =
            agendamentoService
                .contarAgendamentosDoProximoDia();

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/calendario-mes-atual")
    public ResponseEntity<
        List<AgendamentoCalendarioDiaResponse>
    > listarCalendarioMesAtual() {

        List<AgendamentoCalendarioDiaResponse>
            response =
                agendamentoService
                    .listarCalendarioMesAtual();

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, String>>
        criarRespostaErro(
            IllegalArgumentException exception
        ) {
        return ResponseEntity
            .badRequest()
            .body(
                Map.of(
                    "mensagem",
                    exception.getMessage()
                )
            );
    }
}