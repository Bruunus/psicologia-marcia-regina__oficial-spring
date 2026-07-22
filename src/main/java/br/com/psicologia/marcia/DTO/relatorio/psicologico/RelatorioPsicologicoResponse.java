package br.com.psicologia.marcia.DTO.relatorio.psicologico;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record RelatorioPsicologicoResponse(
        Long id,
        Long pacienteId,
        String nomePaciente,
        String mesAcompanhamento,
        Integer anoAcompanhamento,
        String abordagem,
        Integer ocorrencia,
        String formato,
        String analise,
        List<String> hipotesesDiagnosticas,
        String cidadeEmissao,
        Integer diaEmissao,
        String mesEmissao,
        Integer anoEmissao,
        LocalDate dataEmissaoSistema,
        LocalDateTime dataCriacao
) {
}