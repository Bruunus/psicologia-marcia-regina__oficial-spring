
package br.com.psicologia.marcia.DTO.queixa;

import jakarta.validation.constraints.NotNull;

public record QueixaVisualizarRequest(

    @NotNull(message = "O ID da queixa é obrigatório")
    Long queixaId

) {
}
