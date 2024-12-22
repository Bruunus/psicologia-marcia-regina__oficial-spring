package br.com.psicologia.marcia.DTO.paciente;

import java.time.LocalDate;

import br.com.psicologia.marcia.DTO.endereco.EnderecoRecord;
import br.com.psicologia.marcia.DTO.queixa.QueixaRecord;
import br.com.psicologia.marcia.model.Perfil;
import br.com.psicologia.marcia.model.StatusPaciente;

public record PacienteRecord(
		
		
		Long id,
		String nomeCompleto, 
		String cpf, 
		String email, 
		String telefone,
		String telefoneContato,
		Short idade, 
		LocalDate dataNascimento, 
		String estadoCivil, 
		Boolean filhos, 
		Short qtdFilhos,
		String grauEscolaridade, 
		String profissao,
		StatusPaciente statusPaciente,
		Perfil perfil,
		EnderecoRecord endereco,
		QueixaRecord queixa 
		
		) {}
