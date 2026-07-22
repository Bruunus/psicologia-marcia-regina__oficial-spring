package br.com.psicologia.marcia.service.relatorio.psicologico;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.DTO.relatorio.psicologico.RelatorioPsicologicoRequest;
import br.com.psicologia.marcia.DTO.relatorio.psicologico.RelatorioPsicologicoResponse;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.RelatorioPsicologico;
import br.com.psicologia.marcia.model.RelatorioPsicologicoHipoteseDiagnostica;
import br.com.psicologia.marcia.repository.paciente.ReadPacienteRepository;
import br.com.psicologia.marcia.repository.relatorio.psicologico.RelatorioPsicologicoRepository;

@Service
public class RelatorioPsicologicoService {

    @Autowired
    private RelatorioPsicologicoRepository relatorioPsicologicoRepository;

    @Autowired
    private ReadPacienteRepository readPacienteRepository;

    @Transactional
    public RelatorioPsicologicoResponse salvar(RelatorioPsicologicoRequest request) {
        Paciente paciente = readPacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        RelatorioPsicologico relatorio = new RelatorioPsicologico();

        relatorio.setPaciente(paciente);
        relatorio.setMesAcompanhamento(request.mesAcompanhamento());
        relatorio.setAnoAcompanhamento(request.anoAcompanhamento());
        relatorio.setAbordagem(request.abordagem());
        relatorio.setOcorrencia(request.ocorrencia());
        relatorio.setFormato(request.formato());
        relatorio.setAnalise(request.analise());
        relatorio.setCidadeEmissao(request.cidadeEmissao());
        relatorio.setDiaEmissao(request.diaEmissao());
        relatorio.setMesEmissao(request.mesEmissao());
        relatorio.setAnoEmissao(request.anoEmissao());

        for (String descricao : request.hipotesesDiagnosticas()) {
            if (descricao != null && !descricao.trim().isEmpty()) {
                RelatorioPsicologicoHipoteseDiagnostica hipotese = new RelatorioPsicologicoHipoteseDiagnostica();
                hipotese.setDescricao(descricao.trim());
                relatorio.adicionarHipoteseDiagnostica(hipotese);
            }
        }

        if (relatorio.getHipotesesDiagnosticas().isEmpty()) {
            throw new RuntimeException("Informe pelo menos uma hipótese diagnóstica válida");
        }

        RelatorioPsicologico relatorioSalvo = relatorioPsicologicoRepository.save(relatorio);

        return converterParaResponse(relatorioSalvo);
    }

    public List<RelatorioPsicologicoResponse> listarPorPaciente(Long pacienteId) {
        List<RelatorioPsicologico> relatorios = relatorioPsicologicoRepository.findByPacienteIdOrderByDataCriacaoDesc(pacienteId);

        return relatorios.stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public RelatorioPsicologicoResponse buscarPorId(Long relatorioId) {
        RelatorioPsicologico relatorio = relatorioPsicologicoRepository.findById(relatorioId)
                .orElseThrow(() -> new RuntimeException("Relatório psicológico não encontrado"));

        return converterParaResponse(relatorio);
    }

    private RelatorioPsicologicoResponse converterParaResponse(RelatorioPsicologico relatorio) {
        List<String> hipoteses = relatorio.getHipotesesDiagnosticas()
                .stream()
                .map(RelatorioPsicologicoHipoteseDiagnostica::getDescricao)
                .toList();

        return new RelatorioPsicologicoResponse(
                relatorio.getId(),
                relatorio.getPaciente().getId(),
                relatorio.getPaciente().getNomeCompleto(),
                relatorio.getMesAcompanhamento(),
                relatorio.getAnoAcompanhamento(),
                relatorio.getAbordagem(),
                relatorio.getOcorrencia(),
                relatorio.getFormato(),
                relatorio.getAnalise(),
                hipoteses,
                relatorio.getCidadeEmissao(),
                relatorio.getDiaEmissao(),
                relatorio.getMesEmissao(),
                relatorio.getAnoEmissao(),
                relatorio.getDataEmissaoSistema(),
                relatorio.getDataCriacao()
        );
    }
}