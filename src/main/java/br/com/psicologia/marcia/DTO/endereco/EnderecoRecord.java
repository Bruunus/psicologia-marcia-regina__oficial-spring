package br.com.psicologia.marcia.DTO.endereco;

public record EnderecoRecord(
		Long id,
		String logradouro,
		String numero,
		String complemento,
		String bairro,
		String cidade,
		String uf,
		String cep
		) {

}
