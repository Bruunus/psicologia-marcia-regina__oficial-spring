package br.com.psicologia.marcia.service.paciente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.DTO.PacienteRecord;
import br.com.psicologia.marcia.model.Endereco;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.Queixa;
import br.com.psicologia.marcia.repository.paciente.CreatePacienteRepository;
import br.com.psicologia.marcia.repository.paciente.cadastro.EnderecoRepository;
import br.com.psicologia.marcia.repository.paciente.cadastro.QueixaRepository;

@Service
public class CreatePacienteService {
		 
	
	
	private PacienteValidation pacienteValidation;
	
	@Autowired
	private CreatePacienteRepository createPacienteRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private QueixaRepository queixaRepository;

	
	
	public Boolean cadastrarPaciente(PacienteRecord pacienteRecord) {
		
//		pacienteValidation = new PacienteValidation();		 
//		if(pacienteValidation.validacaoNaoPodeSerIgual(pacienteDTO)) {
//			
//			System.out.println("Retorno false");
//			return false;
//		} else {
						
//			Ainda em implementação
			
//			System.out.println("Retorno true");
			
			try {
				Paciente paciente = new Paciente();				
				
				paciente.setNomeCompleto(pacienteRecord.nomeCompleto());
				paciente.setCpf(pacienteRecord.cpf());
				paciente.setEmail(pacienteRecord.email());
				paciente.setTelefone(pacienteRecord.telefone());
				paciente.setTelefoneContato(pacienteRecord.telefoneContato());
				paciente.setIdade(pacienteRecord.idade());
				paciente.setDataNascimento(pacienteRecord.dataNascimento());
				paciente.setEstadoCivil(pacienteRecord.estadoCivil());
				paciente.setFilhos(pacienteRecord.filhos());
				paciente.setQtdFilhos(pacienteRecord.qtdFilhos());
				paciente.setGrauEscolaridade(pacienteRecord.grauEscolaridade());
				paciente.setProfissao(pacienteRecord.profissao());
				paciente.setPerfil(pacienteRecord.perfil());
				
				System.out.println(pacienteRecord.dataNascimento());
				
				
//				if (pacienteRecord.endereco() != null) {
					Endereco endereco = new Endereco();
					
	                endereco.setRua(pacienteRecord.endereco().rua());
	                endereco.setNumero(pacienteRecord.endereco().numero());
	                endereco.setComplemento(pacienteRecord.endereco().complemento());
	                endereco.setBairro(pacienteRecord.endereco().bairro());
	                endereco.setCidade(pacienteRecord.endereco().cidade());
	                endereco.setUf(pacienteRecord.endereco().uf());
	                endereco.setCep(pacienteRecord.endereco().cep());
//	                endereco.setPaciente(pacienteRecord.endereco().pacienteRecord());
	                
	                
	                
	                
//	            }
				
//				if (pacienteRecord.queixa() != null) {
					Queixa queixa = new Queixa();
	                queixa.setQueixa(pacienteRecord.queixa().queixa());
	                
	                
	                
	                ;
//	            }
								
				// salva primeiro por causa do relacionamento bidirecional
				endereco = enderecoRepository.save(endereco);
				paciente.setEndereco(endereco);
				
				queixa = queixaRepository.save(queixa);
				paciente.setQueixa(queixa);				
				
				createPacienteRepository.save(paciente);
				
				System.out.println("Cadastro realizado com sucesso");
				return true;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e.getMessage());
				e.getLocalizedMessage();
				
				return false;
			}
			
			
			
			
			
			
			
			
		}
		
		

		
		
		 
//	}

}
