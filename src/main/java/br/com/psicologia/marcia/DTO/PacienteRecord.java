package br.com.psicologia.marcia.DTO;

import java.time.LocalDate;

import br.com.psicologia.marcia.model.Perfil;

public record PacienteRecord(
		String nomeCompleto, 
		String cpf, 
		String email, 
		String telefone,
		String telefoneContato,
		Short idade, 
		LocalDate dataNascimento, 
		String estadoCivil, 
		String filhos, 
		Short qtdFilhos,
		String grauEscolaridade, 
		String profissao, 
		Perfil perfil,
		String queixa,
		
		
		String rua,
		String numero,
		String complemento,
		String Bairro,
		String cidade,
		String uf,
		String cep
		
		) {}
