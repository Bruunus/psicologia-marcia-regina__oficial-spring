package br.com.psicologia.marcia.service.acompanhamento;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.DTO.acompanhamento.AcompanhamentoPacienteResponseDTO;
import br.com.psicologia.marcia.DTO.acompanhamento.CriarAcompanhamentoPacienteDTO;
import br.com.psicologia.marcia.model.AcompanhamentoPaciente;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.enums.StatusDelete;
import br.com.psicologia.marcia.repository.acompanhamento.AcompanhamentoPacienteRepository;
import br.com.psicologia.marcia.repository.paciente.ReadPacienteRepository;

@Service
public class AcompanhamentoPacienteService {

    private final AcompanhamentoPacienteRepository acompanhamentoPacienteRepository;
    private final ReadPacienteRepository readPacienteRepository;

    public AcompanhamentoPacienteService(
            AcompanhamentoPacienteRepository acompanhamentoPacienteRepository,
            ReadPacienteRepository readPacienteRepository
    ) {
        this.acompanhamentoPacienteRepository = acompanhamentoPacienteRepository;
        this.readPacienteRepository = readPacienteRepository;
    }

    @Transactional
    public AcompanhamentoPacienteResponseDTO criar(
            Long pacienteId,
            CriarAcompanhamentoPacienteDTO dto
    ) {
        Paciente paciente = buscarPacientePorId(pacienteId);

        LocalDateTime dataHoraAtual = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));

        AcompanhamentoPaciente acompanhamentoPaciente = new AcompanhamentoPaciente();

        acompanhamentoPaciente.setPaciente(paciente);
        acompanhamentoPaciente.setSigiloEtico(normalizarTextoPermitindoVazio(dto.getSigiloEtico()));
        acompanhamentoPaciente.setAcompanhamento(normalizarTextoPermitindoVazio(dto.getAcompanhamento()));
        acompanhamentoPaciente.setDataAcompanhamento(dataHoraAtual);
        acompanhamentoPaciente.setPacienteAusente(false);
        acompanhamentoPaciente.setStatusDelete(StatusDelete.NAO_DELETADO);

        AcompanhamentoPaciente acompanhamentoSalvo =
                acompanhamentoPacienteRepository.save(acompanhamentoPaciente);

        return new AcompanhamentoPacienteResponseDTO(acompanhamentoSalvo);
    }

    @Transactional(readOnly = true)
    public List<AcompanhamentoPacienteResponseDTO> listarTodos() {
        return acompanhamentoPacienteRepository
                .findByStatusDeleteOrderByDataAcompanhamentoDesc(StatusDelete.NAO_DELETADO)
                .stream()
                .map(AcompanhamentoPacienteResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AcompanhamentoPacienteResponseDTO> listarPorPaciente(Long pacienteId) {
        buscarPacientePorId(pacienteId);

        return acompanhamentoPacienteRepository
                .findByPacienteIdAndStatusDeleteOrderByDataAcompanhamentoDesc(
                        pacienteId,
                        StatusDelete.NAO_DELETADO
                )
                .stream()
                .map(AcompanhamentoPacienteResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public AcompanhamentoPacienteResponseDTO buscarPorId(Long acompanhamentoId) {
        AcompanhamentoPaciente acompanhamentoPaciente =
                buscarAcompanhamentoAtivoPorId(acompanhamentoId);

        return new AcompanhamentoPacienteResponseDTO(acompanhamentoPaciente);
    }

    @Transactional
    public AcompanhamentoPacienteResponseDTO atualizar(
            Long acompanhamentoId,
            CriarAcompanhamentoPacienteDTO dto
    ) {
        AcompanhamentoPaciente acompanhamentoPaciente =
                buscarAcompanhamentoAtivoPorId(acompanhamentoId);

        acompanhamentoPaciente.setSigiloEtico(normalizarTextoPermitindoVazio(dto.getSigiloEtico()));
        acompanhamentoPaciente.setAcompanhamento(normalizarTextoPermitindoVazio(dto.getAcompanhamento()));

        AcompanhamentoPaciente acompanhamentoAtualizado =
                acompanhamentoPacienteRepository.save(acompanhamentoPaciente);

        return new AcompanhamentoPacienteResponseDTO(acompanhamentoAtualizado);
    }

    @Transactional
    public void excluirFalso(Long acompanhamentoId) {
        AcompanhamentoPaciente acompanhamentoPaciente =
                buscarAcompanhamentoAtivoPorId(acompanhamentoId);

        acompanhamentoPaciente.setStatusDelete(StatusDelete.DELETADO);

        acompanhamentoPacienteRepository.save(acompanhamentoPaciente);
    }

    @Transactional
    public AcompanhamentoPacienteResponseDTO registrarAusencia(Long acompanhamentoId) {
        AcompanhamentoPaciente acompanhamentoPaciente =
                buscarAcompanhamentoAtivoPorId(acompanhamentoId);

        acompanhamentoPaciente.setPacienteAusente(true);

        AcompanhamentoPaciente acompanhamentoAtualizado =
                acompanhamentoPacienteRepository.save(acompanhamentoPaciente);

        return new AcompanhamentoPacienteResponseDTO(acompanhamentoAtualizado);
    }

    @Transactional
    public AcompanhamentoPacienteResponseDTO cancelarAusencia(Long acompanhamentoId) {
        AcompanhamentoPaciente acompanhamento =
                buscarAcompanhamentoAtivoPorId(acompanhamentoId);

        acompanhamento.setPacienteAusente(false);

        AcompanhamentoPaciente acompanhamentoAtualizado =
                acompanhamentoPacienteRepository.save(acompanhamento);

        return new AcompanhamentoPacienteResponseDTO(acompanhamentoAtualizado);
    }

    @Transactional(readOnly = true)
    public List<AcompanhamentoPacienteResponseDTO> listarParaHistorico(Long pacienteId) {
        buscarPacientePorId(pacienteId);

        return acompanhamentoPacienteRepository
                .findByPacienteIdAndPacienteAusenteFalseAndStatusDeleteOrderByDataAcompanhamentoDesc(
                        pacienteId,
                        StatusDelete.NAO_DELETADO
                )
                .stream()
                .map(AcompanhamentoPacienteResponseDTO::new)
                .toList();
    }

    private Paciente buscarPacientePorId(Long pacienteId) {
        return readPacienteRepository
                .findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado."));
    }

    private AcompanhamentoPaciente buscarAcompanhamentoAtivoPorId(Long acompanhamentoId) {
        return acompanhamentoPacienteRepository
                .findByIdAndStatusDelete(acompanhamentoId, StatusDelete.NAO_DELETADO)
                .orElseThrow(() -> new RuntimeException("Acompanhamento não encontrado."));
    }

    private String normalizarTextoPermitindoVazio(String texto) {
        return texto == null ? "" : texto;
    }
}