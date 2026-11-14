package br.com.psicologia.marcia.service.usuario;

import org.springframework.http.HttpStatus;

public class UsuarioAdministracaoException extends RuntimeException {

    private final HttpStatus status;

    public UsuarioAdministracaoException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}