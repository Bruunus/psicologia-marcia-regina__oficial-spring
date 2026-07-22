package br.com.psicologia.marcia.service.declaracao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.psicologia.marcia.DTO.declaracao.DeclaracaoComparecimentoRequest;
import br.com.psicologia.marcia.DTO.declaracao.DeclaracaoComparecimentoResponse;
import br.com.psicologia.marcia.model.DeclaracaoComparecimento;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.enums.OrientacaoDeclaracaoComparecimento;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.repository.declaracao.DeclaracaoComparecimentoRepository;
import br.com.psicologia.marcia.repository.paciente.UpdatePacienteRepository;

@Service
public class DeclaracaoComparecimentoService {

    private final DeclaracaoComparecimentoRepository declaracaoComparecimentoRepository;
    private final UpdatePacienteRepository pacienteRepository;

    public DeclaracaoComparecimentoService(
            DeclaracaoComparecimentoRepository declaracaoComparecimentoRepository,
            UpdatePacienteRepository pacienteRepository
    ) {
        this.declaracaoComparecimentoRepository = declaracaoComparecimentoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional
    public DeclaracaoComparecimentoResponse salvar(DeclaracaoComparecimentoRequest request) {
        validarRequest(request);

        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Paciente não encontrado."
                ));

        DeclaracaoComparecimento declaracao = new DeclaracaoComparecimento();
        declaracao.setPaciente(paciente);
        declaracao.setDataComparecimento(request.getDataComparecimento());
        declaracao.setHoraInicio(definirHoraInicio(request));
        declaracao.setHoraTermino(definirHoraTermino(request));
        declaracao.setOrientacao(request.getOrientacao());
        declaracao.setQuantidadeDiasRepouso(definirQuantidadeDiasRepouso(request));
        declaracao.setDataEmissao(definirDataEmissao(request.getDataEmissao()));
        declaracao.setStatusDelete(StatusDelete.NAO_DELETADO);

        DeclaracaoComparecimento declaracaoSalva = declaracaoComparecimentoRepository.save(declaracao);

        return montarResponse(declaracaoSalva);
    }

    private void validarRequest(DeclaracaoComparecimentoRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dados da declaração não foram enviados."
            );
        }

        if (request.getPacienteId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Paciente é obrigatório."
            );
        }

        if (request.getDataComparecimento() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Data de comparecimento é obrigatória."
            );
        }

        if (request.getOrientacao() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Orientação é obrigatória."
            );
        }

        if (OrientacaoDeclaracaoComparecimento.RETORNAR_AO_TRABALHO.equals(request.getOrientacao())) {
            validarHorasParaRetornoAoTrabalho(request);
            return;
        }

        if (OrientacaoDeclaracaoComparecimento.PERMANECER_EM_REPOUSO.equals(request.getOrientacao())) {
            validarDiasRepouso(request);
        }
    }

    private void validarHorasParaRetornoAoTrabalho(DeclaracaoComparecimentoRequest request) {
        if (request.getHoraInicio() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Hora de início é obrigatória quando a orientação for retornar ao trabalho."
            );
        }

        if (request.getHoraTermino() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Hora de término é obrigatória quando a orientação for retornar ao trabalho."
            );
        }

        if (request.getHoraTermino().isBefore(request.getHoraInicio())
                || request.getHoraTermino().equals(request.getHoraInicio())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Hora de término deve ser maior que a hora de início."
            );
        }
    }

    private void validarDiasRepouso(DeclaracaoComparecimentoRequest request) {
        if (request.getQuantidadeDiasRepouso() == null || request.getQuantidadeDiasRepouso() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantidade de dias de repouso é obrigatória quando a orientação for permanecer em repouso."
            );
        }
    }

    private LocalTime definirHoraInicio(DeclaracaoComparecimentoRequest request) {
        if (OrientacaoDeclaracaoComparecimento.RETORNAR_AO_TRABALHO.equals(request.getOrientacao())) {
            return request.getHoraInicio();
        }

        return null;
    }

    private LocalTime definirHoraTermino(DeclaracaoComparecimentoRequest request) {
        if (OrientacaoDeclaracaoComparecimento.RETORNAR_AO_TRABALHO.equals(request.getOrientacao())) {
            return request.getHoraTermino();
        }

        return null;
    }

    private Integer definirQuantidadeDiasRepouso(DeclaracaoComparecimentoRequest request) {
        if (OrientacaoDeclaracaoComparecimento.PERMANECER_EM_REPOUSO.equals(request.getOrientacao())) {
            return request.getQuantidadeDiasRepouso();
        }

        return null;
    }

    private LocalDate definirDataEmissao(LocalDate dataEmissao) {
        if (dataEmissao != null) {
            return dataEmissao;
        }

        return LocalDate.now(ZoneId.of("America/Sao_Paulo"));
    }

    private DeclaracaoComparecimentoResponse montarResponse(DeclaracaoComparecimento declaracao) {
        return new DeclaracaoComparecimentoResponse(
                declaracao.getId(),
                declaracao.getPaciente().getId(),
                declaracao.getPaciente().getNomeCompleto(),
                declaracao.getDataComparecimento(),
                declaracao.getHoraInicio(),
                declaracao.getHoraTermino(),
                declaracao.getOrientacao(),
                declaracao.getQuantidadeDiasRepouso(),
                declaracao.getDataEmissao(),
                declaracao.getStatusDelete()
        );
    }
}