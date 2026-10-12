package br.com.psicologia.marcia.DTO.laudo;

import jakarta.validation.constraints.NotNull;

public record LaudoVisualizarRequest(

    @NotNull(
        message = "O ID do laudo é obrigatório"
    )
    Long laudoId

) {
}