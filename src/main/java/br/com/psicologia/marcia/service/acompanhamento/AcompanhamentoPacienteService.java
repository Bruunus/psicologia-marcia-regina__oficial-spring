package br.com.psicologia.marcia.service.acompanhamento;

import java.time.LocalDate;
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
    public AcompanhamentoPacienteResponseDTO criar(Long pacienteId, CriarAcompanhamentoPacienteDTO dto) {
        Paciente paciente = buscarPacientePorId(pacienteId);

        LocalDate dataHoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        validarSeJaExisteAcompanhamentoNaData(pacienteId, dataHoje);

        AcompanhamentoPaciente acompanhamentoPaciente = new AcompanhamentoPaciente();
        acompanhamentoPaciente.setPaciente(paciente);
        acompanhamentoPaciente.setSigiloEtico(dto.getSigiloEtico());
        acompanhamentoPaciente.setAcompanhamento(dto.getAcompanhamento());
        acompanhamentoPaciente.setDataAcompanhamento(dataHoje);
        acompanhamentoPaciente.setPacienteAusente(false);
        acompanhamentoPaciente.setStatusDelete(StatusDelete.NAO_DELETADO);

        AcompanhamentoPaciente acompanhamentoSalvo = acompanhamentoPacienteRepository.save(acompanhamentoPaciente);

        return new AcompanhamentoPacienteResponseDTO(acompanhamentoSalvo);
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
        AcompanhamentoPaciente acompanhamentoPaciente = buscarAcompanhamentoAtivoPorId(acompanhamentoId);

        return new AcompanhamentoPacienteResponseDTO(acompanhamentoPaciente);
    }

    @Transactional
    public AcompanhamentoPacienteResponseDTO atualizar(
            Long acompanhamentoId,
            CriarAcompanhamentoPacienteDTO dto
    ) {
        AcompanhamentoPaciente acompanhamentoPaciente = buscarAcompanhamentoAtivoPorId(acompanhamentoId);

        acompanhamentoPaciente.setSigiloEtico(dto.getSigiloEtico());
        acompanhamentoPaciente.setAcompanhamento(dto.getAcompanhamento());

        AcompanhamentoPaciente acompanhamentoAtualizado = acompanhamentoPacienteRepository.save(acompanhamentoPaciente);

        return new AcompanhamentoPacienteResponseDTO(acompanhamentoAtualizado);
    }

    @Transactional
    public void excluirFalso(Long acompanhamentoId) {
        AcompanhamentoPaciente acompanhamentoPaciente = buscarAcompanhamentoAtivoPorId(acompanhamentoId);

        acompanhamentoPaciente.setStatusDelete(StatusDelete.DELETADO);

        acompanhamentoPacienteRepository.save(acompanhamentoPaciente);
    }

    @Transactional
    public AcompanhamentoPacienteResponseDTO registrarAusencia(Long acompanhamentoId) {
        AcompanhamentoPaciente acompanhamentoPaciente = buscarAcompanhamentoAtivoPorId(acompanhamentoId);

        acompanhamentoPaciente.setPacienteAusente(true);

        AcompanhamentoPaciente acompanhamentoAtualizado = acompanhamentoPacienteRepository.save(acompanhamentoPaciente);

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
        return readPacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado."));
    }

    private AcompanhamentoPaciente buscarAcompanhamentoAtivoPorId(Long acompanhamentoId) {
        return acompanhamentoPacienteRepository
                .findByIdAndStatusDelete(acompanhamentoId, StatusDelete.NAO_DELETADO)
                .orElseThrow(() -> new RuntimeException("Acompanhamento não encontrado."));
    }

    private void validarSeJaExisteAcompanhamentoNaData(Long pacienteId, LocalDate dataAcompanhamento) {
        boolean acompanhamentoJaExiste = acompanhamentoPacienteRepository
                .findByPacienteIdAndDataAcompanhamentoAndStatusDelete(
                        pacienteId,
                        dataAcompanhamento,
                        StatusDelete.NAO_DELETADO
                )
                .isPresent();

        if (acompanhamentoJaExiste) {
            throw new RuntimeException("Já existe acompanhamento cadastrado para este paciente na data de hoje.");
        }
    }
}