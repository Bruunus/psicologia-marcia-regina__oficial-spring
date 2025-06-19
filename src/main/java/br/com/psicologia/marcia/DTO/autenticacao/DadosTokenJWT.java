package br.com.psicologia.marcia.DTO.autenticacao;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de resposta para o login, contendo o token JWT, nome do usuário e perfil (role).
 *
 * @param token Token JWT gerado
 * @param nomeUsuario Login do usuário autenticado
 * @param role Perfil do usuário (ex: USER, ADMIN)
 */
public record DadosTokenJWT(
		String token, 
		String login, 
		@JsonProperty("role")
		String perfil) {}


	 
