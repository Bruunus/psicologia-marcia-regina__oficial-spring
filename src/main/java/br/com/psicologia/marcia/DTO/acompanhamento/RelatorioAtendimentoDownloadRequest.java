
package br.com.psicologia.marcia.DTO.acompanhamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RelatorioAtendimentoDownloadRequest(

    @NotNull(message = "O ID do acompanhamento é obrigatório")
    Long acompanhamentoId,

    @NotBlank(message = "O tipo de papel é obrigatório")
    String tipoPapel

) {
}
