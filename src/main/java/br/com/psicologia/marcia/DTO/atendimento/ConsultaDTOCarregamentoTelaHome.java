package br.com.psicologia.marcia.DTO.atendimento;

import java.time.LocalDate;

import br.com.psicologia.marcia.model.enums.Perfil;

public class ConsultaDTOCarregamentoTelaHome {

    private LocalDate dataUltimoAtendimento;
    private Long id;
    private String nomeCompleto;
    private String cpf;
    private Perfil perfil;
    private Short idade;
    private boolean atendimentoIniciado;

    public ConsultaDTOCarregamentoTelaHome(
            LocalDate dataUltimoAtendimento,
            Long id,
            String nomeCompleto,
            String cpf,
            Perfil perfil,
            Short idade,
            boolean atendimentoIniciado
    ) {
        this.dataUltimoAtendimento = dataUltimoAtendimento;
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.perfil = perfil;
        this.idade = idade;
        this.atendimentoIniciado = atendimentoIniciado;
    }

    public LocalDate getDataUltimoAtendimento() {
        return dataUltimoAtendimento;
    }

    public void setDataUltimoAtendimento(LocalDate dataUltimoAtendimento) {
        this.dataUltimoAtendimento = dataUltimoAtendimento;
    }

    public Long getPacienteId() {
        return id;
    }

    public void setPacienteId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public Short getIdade() {
        return idade;
    }

    public void setIdade(Short idade) {
        this.idade = idade;
    }

    public boolean isAtendimentoIniciado() {
        return atendimentoIniciado;
    }

    public void setAtendimentoIniciado(boolean atendimentoIniciado) {
        this.atendimentoIniciado = atendimentoIniciado;
    }
}
