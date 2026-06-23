package br.com.psicologia.marcia.DTO.acompanhamento;

public class DocumentoHistoricoAtendimentoGeradoDTO {

    private byte[] documento;
    private String nomeArquivo;

    public DocumentoHistoricoAtendimentoGeradoDTO() {
    }

    public DocumentoHistoricoAtendimentoGeradoDTO(byte[] documento, String nomeArquivo) {
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