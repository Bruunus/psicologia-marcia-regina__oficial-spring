
package br.com.psicologia.marcia.DTO.queixa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QueixaDownloadRequest(

    @NotNull(message = "O ID da queixa é obrigatório")
    Long queixaId,

    @NotBlank(message = "O tipo de papel é obrigatório")
    String tipoPapel

) {
}
