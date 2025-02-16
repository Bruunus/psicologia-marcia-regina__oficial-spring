package br.com.psicologia.marcia.service.paciente;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.DTO.endereco.EnderecoRecord;
import br.com.psicologia.marcia.DTO.paciente.PacienteRecord;
import br.com.psicologia.marcia.DTO.queixa.QueixaRecord;
import br.com.psicologia.marcia.model.Atendimento;
import br.com.psicologia.marcia.model.Endereco;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.Queixa;
import br.com.psicologia.marcia.model.StatusPaciente;
import br.com.psicologia.marcia.repository.atendimento.AtendimentoRepository;
import br.com.psicologia.marcia.repository.paciente.UpdatePacienteRepository;
import br.com.psicologia.marcia.repository.paciente.cadastro.EnderecoRepository;
import br.com.psicologia.marcia.repository.paciente.cadastro.QueixaRepository;


@Service
public class UpdatePacienteService {

	@Autowired
	private UpdatePacienteRepository updatePacienteRepository;
	
	@Autowired
	private AtendimentoRepository atendimentoRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private QueixaRepository queixaRepository;

	
	@Transactional
	public PacienteRecord updatePatient(PacienteRecord pacienteRecord) {
		
		Paciente paciente = updatePacienteRepository.findById(pacienteRecord.id())
	            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

	
		paciente.setNomeCompleto(pacienteRecord.nomeCompleto());
		paciente.setResponsavel(pacienteRecord.responsavel());
		paciente.setCpf(pacienteRecord.cpf());
		paciente.setRg(pacienteRecord.rg());
		paciente.setEmail(pacienteRecord.email());
		paciente.setTelefone(pacienteRecord.telefone());
		paciente.setTelefoneContato(pacienteRecord.telefoneContato());
		paciente.setNomeDoContato(pacienteRecord.nomeDoContato());
		paciente.setIdade(pacienteRecord.idade());
		paciente.setDataNascimento(pacienteRecord.dataNascimento());
		paciente.setEstadoCivil(pacienteRecord.estadoCivil());
		paciente.setFilhos(pacienteRecord.filhos());
		paciente.setQtdFilhos(pacienteRecord.qtdFilhos());
		paciente.setGrauEscolaridade(pacienteRecord.grauEscolaridade());
		paciente.setProfissao(pacienteRecord.profissao());
		paciente.setStatusPaciente(StatusPaciente.ATIVADO);
		paciente.setPerfil(pacienteRecord.perfil());
		
		
		Atendimento pre_atendimento = new Atendimento();
		
		pre_atendimento.setDataUltimoAtendimento(LocalDate.now());
		pre_atendimento.setPaciente(paciente);
		
		
		Endereco endereco = new Endereco();
		
        endereco.setLogradouro(pacienteRecord.endereco().logradouro());
        endereco.setNumero(pacienteRecord.endereco().numero());
        endereco.setComplemento(pacienteRecord.endereco().complemento());
        endereco.setBairro(pacienteRecord.endereco().bairro());
        endereco.setCidade(pacienteRecord.endereco().cidade());
        endereco.setUf(pacienteRecord.endereco().uf());
        endereco.setCep(pacienteRecord.endereco().cep());
        
        Queixa queixa = new Queixa();
        queixa.setQueixa(pacienteRecord.queixa().queixa());  

		
        endereco = enderecoRepository.save(endereco);
		paciente.setEndereco(endereco);
		
		queixa = queixaRepository.save(queixa);
		paciente.setQueixa(queixa);				
		
		updatePacienteRepository.save(paciente);
		
		pre_atendimento = atendimentoRepository.save(pre_atendimento);
		

		return new PacienteRecord(
		        paciente.getId(), 
		        paciente.getNomeCompleto(), 
		        paciente.getResponsavel(), 
		        paciente.getCpf(), 
		        paciente.getRg(), 
		        paciente.getEmail(), 
		        paciente.getTelefone(), 
		        paciente.getTelefoneContato(), 
		        paciente.getNomeDoContato(), 
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
		        		endereco.getId(),
		                endereco.getLogradouro(),
		                endereco.getNumero(),
		                endereco.getComplemento(),
		                endereco.getBairro(),
		                endereco.getCidade(),
		                endereco.getUf(),
		                endereco.getCep()
		            ),
		            new QueixaRecord(queixa.getQueixa()) 
		    );
		
	}
	
	
	
}
