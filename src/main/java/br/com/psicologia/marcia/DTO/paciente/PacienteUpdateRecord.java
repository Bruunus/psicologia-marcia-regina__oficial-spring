package br.com.psicologia.marcia.DTO.paciente;

import java.time.LocalDate;

import br.com.psicologia.marcia.DTO.endereco.EnderecoRecord;
import br.com.psicologia.marcia.DTO.queixa.QueixaRecord;

public record PacienteUpdateRecord(
		
		Long id,
		String nomeCompleto, 
		String responsavel,
		String cpf, 
		String rg,
		String email, 
		String telefone,
		String telefoneContato,
		String nomeDoContato,
		Short idade, 
		LocalDate dataNascimento, 
		String estadoCivil, 
		Boolean filhos, 
		Short qtdFilhos,
		String grauEscolaridade, 
		String profissao,
		EnderecoRecord endereco,
		QueixaRecord queixa 
		
		) {}
