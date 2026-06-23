package br.com.psicologia.marcia.DTO.acompanhamento;

public class CriarAcompanhamentoPacienteDTO {

    private String sigiloEtico;
    private String acompanhamento;

    public CriarAcompanhamentoPacienteDTO() {
    }

    public CriarAcompanhamentoPacienteDTO(String sigiloEtico, String acompanhamento) {
        this.sigiloEtico = sigiloEtico;
        this.acompanhamento = acompanhamento;
    }

    public String getSigiloEtico() {
        return sigiloEtico;
    }

    public void setSigiloEtico(String sigiloEtico) {
        this.sigiloEtico = sigiloEtico;
    }

    public String getAcompanhamento() {
        return acompanhamento;
    }

    public void setAcompanhamento(String acompanhamento) {
        this.acompanhamento = acompanhamento;
    }
}