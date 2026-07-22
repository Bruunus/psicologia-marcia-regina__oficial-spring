package br.com.psicologia.marcia.DTO.declaracao;

public class DeclaracaoComparecimentoWordRequest {

    private Long declaracaoId;
    private String tipoPapel;

    public Long getDeclaracaoId() {
        return declaracaoId;
    }

    public void setDeclaracaoId(Long declaracaoId) {
        this.declaracaoId = declaracaoId;
    }

    public String getTipoPapel() {
        return tipoPapel;
    }

    public void setTipoPapel(String tipoPapel) {
        this.tipoPapel = tipoPapel;
    }
}