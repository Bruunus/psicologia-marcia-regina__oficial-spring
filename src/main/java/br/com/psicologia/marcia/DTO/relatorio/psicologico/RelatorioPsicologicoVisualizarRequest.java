package br.com.psicologia.marcia.DTO.relatorio.psicologico;

import jakarta.validation.constraints.NotNull;

public record RelatorioPsicologicoVisualizarRequest(

        @NotNull(message = "O ID do relatório psicológico é obrigatório")
        Long relatorioId
) {
}