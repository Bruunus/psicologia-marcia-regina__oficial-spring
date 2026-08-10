package br.com.psicologia.marcia.DTO.laudo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LaudoResponse(

    Long id,

    Long pacienteId,

    String pacienteNome,

    String dominanciaManual,

    LocalDate periodoAvaliacaoInicio,

    LocalDate periodoAvaliacaoFim,

    String encaminhamento,

    String queixa,

    Integer quantidadeSessoes,

    String periodo,

    Integer duracaoMinutos,

    String testesPsicologicos,

    String tarefasEstudos,

    String escalasQuestionarios,

    String dadosAnamnese,

    String dadosAtuais,

    String observacoesClinicas,

    String eficienciaIntelectual,

    String conclusao,

    Boolean incluirEficienciaIntelectual,

    Boolean incluirEscalaWasi,

    Boolean incluirFuncoesExecutivasAtencionais,

    Boolean incluirMemoriaVisualAuditivoVerbal,

    Boolean incluirMemoriaAprendizagem,

    Boolean incluirFuncoesVisoespaciais,

    Boolean incluirEscalaSrs2,

    LocalDateTime dataCriacao,

    LocalDateTime dataAtualizacao

) {
}