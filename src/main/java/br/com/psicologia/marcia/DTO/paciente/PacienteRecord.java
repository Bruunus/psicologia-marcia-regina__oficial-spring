package br.com.psicologia.marcia.DTO.paciente;

import java.time.LocalDate;

import br.com.psicologia.marcia.DTO.endereco.EnderecoRecord;
import br.com.psicologia.marcia.DTO.queixa.QueixaRecord;
import br.com.psicologia.marcia.model.enums.Perfil;
import br.com.psicologia.marcia.model.enums.StatusPaciente;

public record PacienteRecord(
		
		
		Long id,
		String nomeCompleto, 
		String responsavel,
		String cpf, 		
		String rg,
		String orgaoSsp,
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
		StatusPaciente statusPaciente,
		Perfil perfil,
		EnderecoRecord endereco,
		QueixaRecord queixa 
		
		) {}
