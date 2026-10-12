package br.com.psicologia.marcia.DTO.arquivo;

public record PacienteArquivadoResponse(
        Long pacienteId,
        String nomeCompleto,
        String cpf
) {
}