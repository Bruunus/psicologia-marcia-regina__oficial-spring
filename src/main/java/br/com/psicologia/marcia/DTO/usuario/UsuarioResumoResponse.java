package br.com.psicologia.marcia.DTO.usuario;

import br.com.psicologia.marcia.model.enums.StatusUsuario;
import br.com.psicologia.marcia.model.Usuario;

public class UsuarioResumoResponse {

    private Long id;
    private String login;
    private String email;
    private String role;
    private StatusUsuario statusUsuario;

    public UsuarioResumoResponse(
            Long id,
            String login,
            String email,
            String role,
            StatusUsuario statusUsuario
    ) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.role = role;
        this.statusUsuario = statusUsuario;
    }

    public static UsuarioResumoResponse fromEntity(Usuario usuario) {
        return new UsuarioResumoResponse(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getEmail(),
                usuario.getRole() == null ? null : usuario.getRole().name(),
                usuario.getStatusUsuario()
        );
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public StatusUsuario getStatusUsuario() {
        return statusUsuario;
    }

}