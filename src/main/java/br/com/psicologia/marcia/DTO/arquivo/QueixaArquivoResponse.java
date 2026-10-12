package br.com.psicologia.marcia.DTO.arquivo;

import java.time.LocalDate;

public record QueixaArquivoResponse(
        Long documentoId,
        LocalDate dataDocumento,
        String conteudo
) {
}