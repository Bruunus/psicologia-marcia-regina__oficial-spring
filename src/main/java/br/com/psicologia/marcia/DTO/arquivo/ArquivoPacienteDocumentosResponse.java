package br.com.psicologia.marcia.DTO.arquivo;

import java.util.List;

import br.com.psicologia.marcia.DTO.declaracao.DeclaracaoComparecimentoResponse;
import br.com.psicologia.marcia.DTO.laudo.LaudoResponse;
import br.com.psicologia.marcia.DTO.relatorio.psicologico.RelatorioPsicologicoResponse;

public record ArquivoPacienteDocumentosResponse(
        Long pacienteId,
        String nomeCompleto,
        List<QueixaArquivoResponse> queixas,
        List<RelatorioPsicologicoResponse> relatoriosPsicologicos,
        List<DeclaracaoComparecimentoResponse> declaracoesComparecimento,
        List<RelatorioAtendimentoArquivoResponse> relatoriosAtendimento,
        List<ReciboArquivoResponse> recibos,
        List<LaudoResponse> laudos
) {
}