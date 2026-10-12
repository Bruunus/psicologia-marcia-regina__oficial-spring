package br.com.psicologia.marcia.DTO.laudo;

import jakarta.validation.constraints.NotNull;

public record LaudoPacienteRequest(

    @NotNull(
        message = "O ID do paciente é obrigatório"
    )
    Long pacienteId

) {
}