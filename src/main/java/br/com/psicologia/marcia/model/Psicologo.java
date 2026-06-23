package br.com.psicologia.marcia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "psicologo")
public class Psicologo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "funcao_empresa", nullable = false, length = 200)
    private String funcaoEmpresa;

    @Column(name = "crp", nullable = false, length = 30)
    private String crp;

    @Column(name = "telefone", nullable = false, length = 30)
    private String telefone;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    public Psicologo() {
    }

    public Psicologo(
            String nome,
            String funcaoEmpresa,
            String crp,
            String telefone,
            String email
    ) {
        this.nome = nome;
        this.funcaoEmpresa = funcaoEmpresa;
        this.crp = crp;
        this.telefone = telefone;
        this.email = email;
        this.ativo = true;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getFuncaoEmpresa() {
        return funcaoEmpresa;
    }

    public String getCrp() {
        return crp;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setFuncaoEmpresa(String funcaoEmpresa) {
        this.funcaoEmpresa = funcaoEmpresa;
    }

    public void setCrp(String crp) {
        this.crp = crp;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}