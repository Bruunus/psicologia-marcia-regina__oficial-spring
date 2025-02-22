package br.com.psicologia.marcia.service.paciente;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.DTO.endereco.EnderecoRecord;
import br.com.psicologia.marcia.DTO.paciente.PacienteRecord;
import br.com.psicologia.marcia.DTO.paciente.PacienteUpdateRecord;
import br.com.psicologia.marcia.DTO.queixa.QueixaRecord;
import br.com.psicologia.marcia.model.Atendimento;
import br.com.psicologia.marcia.model.Endereco;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.Queixa;
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
	public PacienteUpdateRecord updatePatient(PacienteUpdateRecord pacienteUpdateRecord) {
		
		Paciente paciente = updatePacienteRepository.findById(pacienteUpdateRecord.id())
	            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

	
		paciente.setNomeCompleto(pacienteUpdateRecord.nomeCompleto());
		paciente.setResponsavel(pacienteUpdateRecord.responsavel());
		paciente.setCpf(pacienteUpdateRecord.cpf());
		paciente.setRg(pacienteUpdateRecord.rg());
		paciente.setEmail(pacienteUpdateRecord.email());
		paciente.setTelefone(pacienteUpdateRecord.telefone());
		paciente.setTelefoneContato(pacienteUpdateRecord.telefoneContato());
		paciente.setNomeDoContato(pacienteUpdateRecord.nomeDoContato());
		paciente.setIdade(pacienteUpdateRecord.idade());
		paciente.setDataNascimento(pacienteUpdateRecord.dataNascimento());
		paciente.setEstadoCivil(pacienteUpdateRecord.estadoCivil());
		paciente.setFilhos(pacienteUpdateRecord.filhos());
		paciente.setQtdFilhos(pacienteUpdateRecord.qtdFilhos());
		paciente.setGrauEscolaridade(pacienteUpdateRecord.grauEscolaridade());
		paciente.setProfissao(pacienteUpdateRecord.profissao());
		
		
		Atendimento pre_atendimento = new Atendimento();
		
		pre_atendimento.setId(pacienteUpdateRecord.id());
		pre_atendimento.setDataUltimoAtendimento(LocalDate.now());
		pre_atendimento.setPaciente(paciente);
		
		
		Endereco endereco = new Endereco();
		
		endereco.setId(pacienteUpdateRecord.endereco().id());
        endereco.setLogradouro(pacienteUpdateRecord.endereco().logradouro());
        endereco.setNumero(pacienteUpdateRecord.endereco().numero());
        endereco.setComplemento(pacienteUpdateRecord.endereco().complemento());
        endereco.setBairro(pacienteUpdateRecord.endereco().bairro());
        endereco.setCidade(pacienteUpdateRecord.endereco().cidade());
        endereco.setUf(pacienteUpdateRecord.endereco().uf());
        endereco.setCep(pacienteUpdateRecord.endereco().cep());
        
        Queixa queixa = new Queixa();
        queixa.setId(pacienteUpdateRecord.id()); // amarrando a queixa ao paciente
        queixa.setQueixa(pacienteUpdateRecord.queixa().queixa());  

		
        endereco = enderecoRepository.save(endereco);
		paciente.setEndereco(endereco);
		
		queixa = queixaRepository.save(queixa);
		paciente.setQueixa(queixa);				
		
		updatePacienteRepository.save(paciente);
		
		pre_atendimento = atendimentoRepository.save(pre_atendimento);
		

		return new PacienteUpdateRecord(
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
		            new QueixaRecord(
		            		queixa.getId(),
		            		queixa.getQueixa()
		            		) 
		    );
		
	}
	
	
	
}
