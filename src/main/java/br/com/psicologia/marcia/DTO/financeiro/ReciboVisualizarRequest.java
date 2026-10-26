
package br.com.psicologia.marcia.DTO.financeiro;

import jakarta.validation.constraints.NotNull;

public record ReciboVisualizarRequest(

    @NotNull(message = "O ID do pagamento é obrigatório")
    Long pagamentoId

) {
}
