package br.com.psicologia.marcia.error;

import org.springframework.http.HttpStatus;

public class ApiNegocioException extends RuntimeException {

    private final HttpStatus status;
    private final String codigo;

    public ApiNegocioException(
            HttpStatus status,
            String codigo,
            String mensagem
    ) {
        super(mensagem);
        this.status = status;
        this.codigo = codigo;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCodigo() {
        return codigo;
    }
}
