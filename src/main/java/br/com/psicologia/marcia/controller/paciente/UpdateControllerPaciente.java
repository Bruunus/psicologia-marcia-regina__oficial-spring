package br.com.psicologia.marcia.controller.paciente;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.paciente.PacienteUpdateRecord;
import br.com.psicologia.marcia.service.paciente.UpdatePacienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/paciente")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UpdateControllerPaciente {

    @Autowired
    private UpdatePacienteService updatePacienteService;

    /**
     * Atualiza os dados de um paciente existente.
     *
     * @param pacienteRecord dados atualizados do paciente recebidos do frontend
     * @return ResponseEntity contendo os dados atualizados ou mensagem de erro
     */
    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarPaciente(@RequestBody @Valid PacienteUpdateRecord pacienteRecord) {
        try {
            PacienteUpdateRecord pacienteAtualizado = updatePacienteService.updatePatient(pacienteRecord);

            if (pacienteAtualizado != null) {
                return ResponseEntity.ok(pacienteAtualizado);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Paciente não encontrado para atualização."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("message", "Erro ao atualizar o paciente."));
        }
    }
}
