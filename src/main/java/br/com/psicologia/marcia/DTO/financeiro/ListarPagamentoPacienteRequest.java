package br.com.psicologia.marcia.DTO.financeiro;

public class ListarPagamentoPacienteRequest {

    private Long pacienteId;

    public ListarPagamentoPacienteRequest() {
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }
}