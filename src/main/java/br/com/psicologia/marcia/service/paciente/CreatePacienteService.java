package br.com.psicologia.marcia.service.paciente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
		 
	
	@Autowired
	private PacienteValidationService pacienteValidationService;
	
	@Autowired
	private CreatePacienteRepository createPacienteRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private QueixaRepository queixaRepository;

	
	
	public Integer cadastrarPaciente(PacienteRecord pacienteRecord) {
		
		System.out.println("Antes de enviar o nome: "+pacienteRecord.nomeCompleto());
		
		Integer response = 
				pacienteValidationService.validacaoNaoPodeSerIgual(pacienteRecord.nomeCompleto(), pacienteRecord.cpf());
		
//		pacienteValidationService = new PacienteValidationService();		 
		if(response != null) {
			return response; /* Mudado para teste */
		} else {
			 
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
			
			
//					if (pacienteRecord.endereco() != null) {
			Endereco endereco = new Endereco();
			
            endereco.setLogradouro(pacienteRecord.endereco().logradouro());
            endereco.setNumero(pacienteRecord.endereco().numero());
            endereco.setComplemento(pacienteRecord.endereco().complemento());
            endereco.setBairro(pacienteRecord.endereco().bairro());
            endereco.setCidade(pacienteRecord.endereco().cidade());
            endereco.setUf(pacienteRecord.endereco().uf());
            endereco.setCep(pacienteRecord.endereco().cep());
//		                endereco.setPaciente(pacienteRecord.endereco().pacienteRecord());
 
			Queixa queixa = new Queixa();
            queixa.setQueixa(pacienteRecord.queixa().queixa());  
            
//	            Teste do catch 
//	            if(true)
//	            	throw new Exception("Este é um teste");
               
							
			// salva primeiro por causa do relacionamento bidirecional
			endereco = enderecoRepository.save(endereco);
			paciente.setEndereco(endereco);
			
			queixa = queixaRepository.save(queixa);
			paciente.setQueixa(queixa);				
			
			createPacienteRepository.save(paciente);
			
			
			
			System.out.println("Cadastro realizado com sucesso no servidor!");
			return 200;
			
					
			
		}
			
		
	}
		
		

		
		
		 
 

}
