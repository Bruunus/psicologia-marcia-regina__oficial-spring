package br.com.psicologia.marcia.DTO;

public record EnderecoRecord(
		Long id,
		String rua,
		String numero,
		String complemento,
		String bairro,
		String cidade,
		String uf,
		String cep
		) {

}
