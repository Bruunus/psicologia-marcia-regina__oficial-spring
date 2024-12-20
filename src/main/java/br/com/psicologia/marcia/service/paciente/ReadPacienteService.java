package br.com.psicologia.marcia.service.paciente;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.DTO.EnderecoRecord;
import br.com.psicologia.marcia.DTO.PacienteRecord;
import br.com.psicologia.marcia.DTO.QueixaRecord;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.repository.paciente.ReadPacienteRepository;


@Service
public class ReadPacienteService {
	
	@Autowired
	private ReadPacienteRepository readPacienteRepository;

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
					paciente.getPerfil(),
					paciente.getStatusPaciente(),
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
