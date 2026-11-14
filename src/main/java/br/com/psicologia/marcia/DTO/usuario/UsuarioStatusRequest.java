package br.com.psicologia.marcia.DTO.usuario;

import br.com.psicologia.marcia.model.enums.StatusUsuario;
import jakarta.validation.constraints.NotNull;

public class UsuarioStatusRequest {

    @NotNull
    private StatusUsuario statusUsuario;

    public StatusUsuario getStatusUsuario() {
        return statusUsuario;
    }

    public void setStatusUsuario(StatusUsuario statusUsuario) {
        this.statusUsuario = statusUsuario;
    }

}