package br.com.psicologia.marcia.controller.paciente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.paciente.PacienteUpdateRecord;
import br.com.psicologia.marcia.error.ApiErrorResponse;
import br.com.psicologia.marcia.service.paciente.UpdatePacienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/paciente")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UpdateControllerPaciente {

    private static final Logger logger =
            LoggerFactory.getLogger(UpdateControllerPaciente.class);

    @Autowired
    private UpdatePacienteService updatePacienteService;

    /**
     * Atualiza os dados de um paciente existente.
     *
     * @param pacienteRecord dados atualizados recebidos do frontend
     * @return dados atualizados ou resposta de erro padronizada
     */
    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarPaciente(
            @RequestBody @Valid PacienteUpdateRecord pacienteRecord
    ) {

        try {

            PacienteUpdateRecord pacienteAtualizado =
                    updatePacienteService.updatePatient(pacienteRecord);

            if (pacienteAtualizado == null) {

                return erro(
                        HttpStatus.NOT_FOUND,
                        "paciente_nao_encontrado",
                        "Paciente não encontrado para atualização."
                );
            }

            return ResponseEntity.ok(pacienteAtualizado);

        } catch (Exception e) {

            logger.error(
                    "Falha interna ao atualizar os dados do paciente.",
                    e
            );

            return erro(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "erro_atualizar_paciente",
                    "Não foi possível atualizar os dados do paciente. Tente novamente."
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
