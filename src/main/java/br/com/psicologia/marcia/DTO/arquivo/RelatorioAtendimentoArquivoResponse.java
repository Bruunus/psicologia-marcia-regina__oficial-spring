package br.com.psicologia.marcia.DTO.arquivo;

import java.time.LocalDateTime;

public record RelatorioAtendimentoArquivoResponse(
        Long acompanhamentoId,
        LocalDateTime dataAcompanhamento,
        String dataAcompanhamentoFormatada,
        String horaAcompanhamentoFormatada,
        String sigiloEtico,
        String acompanhamento
) {
}
