package br.com.psicologia.marcia.error;

import org.springframework.http.HttpStatusCode;

public record ApiErrorResponse(
    String tipo,
    int status,
    String codigo,
    String mensagem
) {
    public static ApiErrorResponse criar(
        HttpStatusCode status,
        String codigo,
        String mensagem
    ) {
        return new ApiErrorResponse(
            "API_ERROR",
            status.value(),
            codigo,
            mensagem
        );
    }
}
