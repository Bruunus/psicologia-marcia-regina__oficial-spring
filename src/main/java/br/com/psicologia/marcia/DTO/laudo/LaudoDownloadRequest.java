package br.com.psicologia.marcia.DTO.laudo;

public record LaudoDownloadRequest(
    Long laudoId,
    String tipoPapel,
    Boolean incluirEficienciaIntelectual,
    Boolean incluirEscalaWasi,
    Boolean incluirEscalaSrs2
) {
}