package br.com.psicologia.marcia.DTO.relatorio.psicologico;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RelatorioPsicologicoRequest(

        @NotNull(message = "O ID do paciente é obrigatório")
        Long pacienteId,

        @NotBlank(message = "O mês do acompanhamento é obrigatório")
        String mesAcompanhamento,

        @NotNull(message = "O ano do acompanhamento é obrigatório")
        Integer anoAcompanhamento,

        @NotBlank(message = "A abordagem é obrigatória")
        String abordagem,

        @NotNull(message = "A ocorrência é obrigatória")
        Integer ocorrencia,

        @NotBlank(message = "O formato é obrigatório")
        String formato,

        @NotBlank(message = "A análise é obrigatória")
        String analise,

        @NotEmpty(message = "Informe pelo menos uma hipótese diagnóstica")
        List<String> hipotesesDiagnosticas,

        @NotBlank(message = "A cidade de emissão é obrigatória")
        String cidadeEmissao,

        @NotNull(message = "O dia de emissão é obrigatório")
        Integer diaEmissao,

        @NotBlank(message = "O mês de emissão é obrigatório")
        String mesEmissao,

        @NotNull(message = "O ano de emissão é obrigatório")
        Integer anoEmissao
) {
}