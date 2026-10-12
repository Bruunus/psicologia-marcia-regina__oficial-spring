package br.com.psicologia.marcia.DTO.laudo;

public record PdfLaudoGeradoDTO(
    byte[] documento,
    String nomeArquivo
) {
}