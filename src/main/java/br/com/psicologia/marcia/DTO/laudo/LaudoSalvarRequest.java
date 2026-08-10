package br.com.psicologia.marcia.DTO.laudo;

import java.time.LocalDate;

public record LaudoSalvarRequest(

    Long pacienteId,

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

    Boolean incluirEscalaSrs2

) {
}