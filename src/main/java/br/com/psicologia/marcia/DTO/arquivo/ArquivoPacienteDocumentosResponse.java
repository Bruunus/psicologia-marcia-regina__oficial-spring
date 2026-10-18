package br.com.psicologia.marcia.DTO.arquivo;

import java.time.LocalDate;
import java.util.List;

import br.com.psicologia.marcia.DTO.declaracao.DeclaracaoComparecimentoResponse;
import br.com.psicologia.marcia.DTO.laudo.LaudoResponse;
import br.com.psicologia.marcia.DTO.relatorio.psicologico.RelatorioPsicologicoResponse;

public record ArquivoPacienteDocumentosResponse(
        Long pacienteId,
        String nomeCompleto,
        String cpf,
        String rg,
        LocalDate dataNascimento,
        Short idade,
        String telefoneContato,
        String enderecoLogradouro,
        String enderecoNumero,
        String enderecoComplemento,
        String enderecoBairro,
        String enderecoCidade,
        String enderecoUf,
        String enderecoCep,
        String psicologoNome,
        String psicologoFuncaoEmpresa,
        String psicologoCrp,
        String psicologoTelefone,
        String psicologoEmail,
        List<QueixaArquivoResponse> queixas,
        List<RelatorioPsicologicoResponse> relatoriosPsicologicos,
        List<DeclaracaoComparecimentoResponse> declaracoesComparecimento,
        List<RelatorioAtendimentoArquivoResponse> relatoriosAtendimento,
        List<ReciboArquivoResponse> recibos,
        List<LaudoResponse> laudos
) {
}