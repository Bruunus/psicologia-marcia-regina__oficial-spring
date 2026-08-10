package br.com.psicologia.marcia.DTO.faturamento;

public class ListarMeuFaturamentoRequest {

    private String nomePaciente;

    public ListarMeuFaturamentoRequest() {
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }
}