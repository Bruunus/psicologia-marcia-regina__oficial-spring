package br.com.psicologia.marcia.DTO.relatorio.psicologico;

public class PdfRelatorioPsicologicoGeradoDTO {

    private byte[] documento;

    private String nomeArquivo;

    public PdfRelatorioPsicologicoGeradoDTO(byte[] documento, String nomeArquivo) {
        this.documento = documento;
        this.nomeArquivo = nomeArquivo;
    }

    public byte[] getDocumento() {
        return documento;
    }

    public void setDocumento(byte[] documento) {
        this.documento = documento;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }
}