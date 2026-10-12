package br.com.psicologia.marcia.DTO.laudo;

public record DocumentoLaudoGeradoDTO(
    byte[] arquivo,
    String nomeArquivo
) {
}