
package br.com.psicologia.marcia.DTO.acompanhamento;

import jakarta.validation.constraints.NotNull;

public record RelatorioAtendimentoVisualizarRequest(

    @NotNull(message = "O ID do acompanhamento é obrigatório")
    Long acompanhamentoId

) {
}
