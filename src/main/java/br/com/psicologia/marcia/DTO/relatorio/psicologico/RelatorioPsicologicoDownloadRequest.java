package br.com.psicologia.marcia.DTO.relatorio.psicologico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RelatorioPsicologicoDownloadRequest(

        @NotNull(message = "O ID do relatório psicológico é obrigatório")
        Long relatorioId,

        @NotBlank(message = "O tipo de papel é obrigatório")
        String tipoPapel
) {
}