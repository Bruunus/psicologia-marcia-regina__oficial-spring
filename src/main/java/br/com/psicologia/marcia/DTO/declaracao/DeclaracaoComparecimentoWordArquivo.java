package br.com.psicologia.marcia.DTO.declaracao;

public class DeclaracaoComparecimentoWordArquivo {

    private final String nomeArquivo;
    private final byte[] conteudo;

    public DeclaracaoComparecimentoWordArquivo(String nomeArquivo, byte[] conteudo) {
        this.nomeArquivo = nomeArquivo;
        this.conteudo = conteudo;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public byte[] getConteudo() {
        return conteudo;
    }
}