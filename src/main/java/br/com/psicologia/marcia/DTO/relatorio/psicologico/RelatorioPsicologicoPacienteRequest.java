package br.com.psicologia.marcia.DTO.relatorio.psicologico;

import jakarta.validation.constraints.NotNull;

public record RelatorioPsicologicoPacienteRequest(

        @NotNull(message = "O ID do paciente é obrigatório")
        Long pacienteId
) {
}