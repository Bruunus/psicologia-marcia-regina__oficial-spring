package br.com.psicologia.marcia.service.paciente;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.DTO.atendimento.ConsultaDTOCarregamentoTelaHome;
import br.com.psicologia.marcia.DTO.endereco.EnderecoRecord;
import br.com.psicologia.marcia.DTO.paciente.PacienteRecord;
import br.com.psicologia.marcia.DTO.queixa.QueixaRecord;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.enums.StatusPaciente;
import br.com.psicologia.marcia.repository.atendimento.AtendimentoRepository;
import br.com.psicologia.marcia.repository.paciente.ReadPacienteRepository;

@Service
public class ReadPacienteService {

    @Autowired
    private ReadPacienteRepository readPacienteRepository;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    public List<ConsultaDTOCarregamentoTelaHome> carregarTelaHome() {

        List<ConsultaDTOCarregamentoTelaHome> list =
                atendimentoRepository.carregarListaHome(
                        StatusPaciente.ATIVADO
                );

        return list;
    }

    public PacienteRecord carregarPaciente(String cpf) {

        Paciente objetoPaciente =
                readPacienteRepository.findByCpf(cpf);

        if (objetoPaciente != null) {
            return new PacienteRecord(
                    objetoPaciente.getId(),
                    objetoPaciente.getNomeCompleto(),
                    objetoPaciente.getResponsavel(),
                    objetoPaciente.getCpf(),
                    objetoPaciente.getRg(),
                    objetoPaciente.getOrgaoSsp(),
                    objetoPaciente.getEmail(),
                    objetoPaciente.getTelefone(),
                    objetoPaciente.getTelefoneContato(),
                    objetoPaciente.getNomeDoContato(),
                    objetoPaciente.getIdade(),
                    objetoPaciente.getDataNascimento(),
                    objetoPaciente.getEstadoCivil(),
                    objetoPaciente.getFilhos(),
                    objetoPaciente.getQtdFilhos(),
                    objetoPaciente.getGrauEscolaridade(),
                    objetoPaciente.getProfissao(),
                    objetoPaciente.getStatusPaciente(),
                    objetoPaciente.getPerfil(),
                    new EnderecoRecord(
                            objetoPaciente.getEndereco().getId(),
                            objetoPaciente.getEndereco().getLogradouro(),
                            objetoPaciente.getEndereco().getNumero(),
                            objetoPaciente.getEndereco().getComplemento(),
                            objetoPaciente.getEndereco().getBairro(),
                            objetoPaciente.getEndereco().getCidade(),
                            objetoPaciente.getEndereco().getUf(),
                            objetoPaciente.getEndereco().getCep()
                    ),
                    new QueixaRecord(
                            objetoPaciente.getId(),
                            objetoPaciente.getQueixa().getQueixa()
                    )
            );
        } else {
            return null;
        }
    }
}