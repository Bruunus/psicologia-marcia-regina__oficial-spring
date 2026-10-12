package br.com.psicologia.marcia.DTO.laudo;

import java.time.LocalDateTime;

public record LaudoListaResponse(
    Long id,
    LocalDateTime dataCriacao
) {
}