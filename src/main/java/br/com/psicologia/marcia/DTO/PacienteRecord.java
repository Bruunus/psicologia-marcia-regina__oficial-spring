package br.com.psicologia.marcia.DTO;

import java.time.LocalDate;

import br.com.psicologia.marcia.model.Endereco;
import br.com.psicologia.marcia.model.Perfil;
import br.com.psicologia.marcia.model.Queixa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
		Perfil perfil,
		EnderecoRecord endereco,
		QueixaRecord queixa 
		
		) {}
