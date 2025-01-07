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
import br.com.psicologia.marcia.repository.atendimento.AtendimentoRepository;
import br.com.psicologia.marcia.repository.paciente.ReadPacienteRepository;


@Service
public class ReadPacienteService {
	
	@Autowired
	private ReadPacienteRepository readPacienteRepository;
	
	@Autowired
	private AtendimentoRepository atendimentoRepository;
	
	
	/**
	 * Método que retorna uma lista personalizada para popular a tabela da tela home dos pacientes.
	 * Esses são colatados da tabela de atendimento, pois todos os dados de atendimento do paciente 
	 * estará vindo desta tabela. Então os dados que serão carregados para tela home são dados associados
	 * ao atendimento do paciente.
	 * @return lista de dados para a tela home de pacientes
	 */
	public List<ConsultaDTOCarregamentoTelaHome> carregarTelaHome() {		
		List<ConsultaDTOCarregamentoTelaHome> list = atendimentoRepository.carregarListaHome();		
		return list;
	}
	
	

	public List<PacienteRecord> carregarListaPaciente() {	
		
		List<PacienteRecord> listaPacientes = new ArrayList<>();
		List<Paciente> list = readPacienteRepository.findAll();		
		
		list.forEach(paciente -> {
			PacienteRecord pacienteRecord = new PacienteRecord(
					paciente.getId(),
					paciente.getNomeCompleto(),
					paciente.getCpf(),
					paciente.getEmail(),
					paciente.getTelefone(),
					paciente.getTelefoneContato(),
					paciente.getIdade(),
					paciente.getDataNascimento(),
					paciente.getEstadoCivil(),
					paciente.getFilhos(),
					paciente.getQtdFilhos(),
					paciente.getGrauEscolaridade(),
					paciente.getProfissao(),
					paciente.getStatusPaciente(),
					paciente.getPerfil(),
					new EnderecoRecord(
							paciente.getEndereco().getId(),
							paciente.getEndereco().getLogradouro(),
							paciente.getEndereco().getNumero(),
							paciente.getEndereco().getComplemento(),
							paciente.getEndereco().getBairro(),
							paciente.getEndereco().getCidade(),
							paciente.getEndereco().getUf(),
							paciente.getEndereco().getCep()
							),
					new QueixaRecord(
							paciente.getQueixa().getQueixa()
							));
			listaPacientes.add(pacienteRecord);
		});		
		
		return listaPacientes;
	}
	
	

}
