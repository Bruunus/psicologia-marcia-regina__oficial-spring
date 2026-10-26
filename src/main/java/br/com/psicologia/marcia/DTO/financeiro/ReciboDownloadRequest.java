
package br.com.psicologia.marcia.DTO.financeiro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReciboDownloadRequest(

    @NotNull(message = "O ID do pagamento é obrigatório")
    Long pagamentoId,

    @NotBlank(message = "O tipo de papel é obrigatório")
    String tipoPapel

) {
}
