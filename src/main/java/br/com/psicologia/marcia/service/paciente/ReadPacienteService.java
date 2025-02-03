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
	
	

	/**
	 * Este serviço é responsável por trazer o paciente solicitado para a tela de 
	 * identificação
	 * @param cpf
	 * @return
	 */
	public PacienteRecord carregarPaciente(String cpf) {	
		
		Paciente objetoPaciente = readPacienteRepository.findByCpf(cpf);
		
		if(objetoPaciente != null) {
			return new PacienteRecord(
					objetoPaciente.getId(),
					objetoPaciente.getNomeCompleto(),
					objetoPaciente.getResponsavel(),
					objetoPaciente.getCpf(),
					objetoPaciente.getRg(),
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
					new QueixaRecord(objetoPaciente.getQueixa().getQueixa())
					);
			
		} else {
			return null;	// verifique o nulo no controller
		}		
		
	}
	
	

}
