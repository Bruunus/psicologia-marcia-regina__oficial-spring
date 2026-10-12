package br.com.psicologia.marcia.DTO.finalizacao;

import jakarta.validation.constraints.NotNull;

public record FinalizarAtendimentoRequest(

        @NotNull(message = "O ID do paciente é obrigatório")
        Long pacienteId

) {
}