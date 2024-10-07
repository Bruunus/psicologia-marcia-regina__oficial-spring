package br.com.psicologia.marcia.DTO;

import java.time.LocalDate;

import br.com.psicologia.marcia.model.Perfil;

public record PacienteRecord(String nomeCompleto, String cpf, String email, String telefone, 
		Short idade, LocalDate dataNascimento, String estadoCivil, Short filhos, 
		String grauEscolaridade, String profissao, String endereco, String queixa, 
		Perfil perfil) {}
