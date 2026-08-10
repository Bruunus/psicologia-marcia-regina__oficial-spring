package br.com.psicologia.marcia.DTO.laudo;

public record DocumentoLaudoGeradoDTO(
    byte[] arquivo,
    String nomeArquivo,
    Boolean incluirEficienciaIntelectual,
    Boolean incluirEscalaWasi,
    Boolean incluirEscalaSrs2
) {
}