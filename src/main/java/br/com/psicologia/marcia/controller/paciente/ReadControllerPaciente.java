package br.com.psicologia.marcia.controller.paciente;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.atendimento.ConsultaDTOCarregamentoTelaHome;
import br.com.psicologia.marcia.DTO.paciente.PacienteRecord;
import br.com.psicologia.marcia.error.ApiErrorResponse;
import br.com.psicologia.marcia.service.paciente.ReadPacienteService;

@RestController
@RequestMapping("/api/paciente")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReadControllerPaciente {

    private static final Logger logger =
            LoggerFactory.getLogger(ReadControllerPaciente.class);

    @Autowired
    private ReadPacienteService readPacienteService;

    @PostMapping("/carregar-tela-home")
    public ResponseEntity<?> carregarDadosTelaHome() {

        try {
            List<ConsultaDTOCarregamentoTelaHome> lista =
                    readPacienteService.carregarTelaHome();

            return ResponseEntity.ok(lista);

        } catch (Exception e) {

            logger.error(
                    "Falha ao carregar dados da tela inicial.",
                    e
            );

            return erro(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "erro_carregar_tela_home",
                    "Não foi possível carregar os pacientes. Tente novamente."
            );
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> pesquisaDePaciente(
            @RequestParam("name") String nome
    ) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/carregar-dados")
    public ResponseEntity<?> getPacientes(
            @RequestBody String cpf
    ) {

        if (cpf == null || cpf.isBlank()) {

            return erro(
                    HttpStatus.BAD_REQUEST,
                    "cpf_obrigatorio",
                    "Informe o CPF do paciente."
            );
        }

        try {
            PacienteRecord paciente =
                    readPacienteService.carregarPaciente(cpf);

            if (paciente == null) {

                return erro(
                        HttpStatus.NOT_FOUND,
                        "paciente_nao_encontrado",
                        "Paciente não encontrado."
                );
            }

            return ResponseEntity.ok(paciente);

        } catch (IllegalArgumentException e) {

            return erro(
                    HttpStatus.BAD_REQUEST,
                    "dados_paciente_invalidos",
                    "Os dados informados para consulta são inválidos."
            );

        } catch (Exception e) {

            logger.error(
                    "Falha ao carregar dados do paciente.",
                    e
            );

            return erro(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "erro_carregar_paciente",
                    "Não foi possível carregar os dados do paciente."
            );
        }
    }

    private ResponseEntity<ApiErrorResponse> erro(
            HttpStatus status,
            String codigo,
            String mensagem
    ) {

        return ResponseEntity
                .status(status)
                .body(
                        ApiErrorResponse.criar(
                                status,
                                codigo,
                                mensagem
                        )
                );
    }
}
