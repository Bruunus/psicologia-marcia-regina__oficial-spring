package br.com.psicologia.marcia.error;

import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import br.com.psicologia.marcia.service.usuario.UsuarioConviteException;
import br.com.psicologia.marcia.service.usuario.UsuarioAdministracaoException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalApiExceptionHandler.class);


    @ExceptionHandler(ApiNegocioException.class)
    public ResponseEntity<ApiErrorResponse> tratarApiNegocio(
            ApiNegocioException exception
    ) {
        return resposta(
                exception.getStatus(),
                exception.getCodigo(),
                exception.getMessage()
        );
    }




    @ExceptionHandler(UsuarioConviteException.class)
    public ResponseEntity<ApiErrorResponse> tratarUsuarioConvite(
        UsuarioConviteException exception
    ) {
        return respostaNegocio(
            exception.getStatus(),
            exception.getMessage()
        );
    }

    @ExceptionHandler(UsuarioAdministracaoException.class)
    public ResponseEntity<ApiErrorResponse> tratarUsuarioAdministracao(
        UsuarioAdministracaoException exception
    ) {
        return respostaNegocio(
            exception.getStatus(),
            exception.getMessage()
        );
    }

    private ResponseEntity<ApiErrorResponse> respostaNegocio(
        HttpStatus status,
        String codigoOriginal
    ) {
        String codigo = codigoOriginal != null
            && codigoOriginal.matches("[a-z][a-z0-9_]{1,79}")
                ? codigoOriginal
                : "erro_negocio";

        String mensagem = switch (codigo) {
            case "cpf_invalido" ->
                "O CPF informado é inválido.";
            case "cpf_obrigatorio" ->
                "Informe o CPF do usuário.";
            case "cpf_deve_conter_11_digitos" ->
                "O CPF deve conter 11 dígitos.";
            case "cpf_ja_cadastrado" ->
                "Este CPF já está cadastrado.";
            case "email_ja_cadastrado" ->
                "Este e-mail já está cadastrado.";
            case "usuario_ja_ativo" ->
                "Este usuário já está ativo.";
            case "usuario_inativo" ->
                "Este usuário está inativo.";
            case "perfil_invalido" ->
                "O perfil informado é inválido.";
            case "cpf_nao_confere_com_usuario_pendente" ->
                "O CPF não confere com o usuário pendente.";
            case "email_nao_confere_com_usuario_pendente" ->
                "O e-mail não confere com o usuário pendente.";
            case "convite_nao_encontrado" ->
                "Convite não encontrado.";
            case "convite_expirado_ou_utilizado" ->
                "Este convite expirou ou já foi utilizado.";
            default -> mensagemPadrao(status);
        };

        return resposta(status, codigo, mensagem);
    }

    private String mensagemPadrao(HttpStatus status) {
        return switch (status.value()) {
            case 400 -> "Os dados enviados são inválidos.";
            case 401 -> "Não foi possível autenticar.";
            case 403 -> "Você não tem permissão para esta operação.";
            case 404 -> "O recurso solicitado não foi encontrado.";
            case 409 -> "Não foi possível concluir a operação devido a um conflito.";
            case 422 -> "Não foi possível concluir a operação. Confira os dados.";
            default -> "Não foi possível concluir a operação.";
        };
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> tratarValidacao(
        MethodArgumentNotValidException exception
    ) {
        return resposta(
            HttpStatus.BAD_REQUEST,
            "dados_invalidos",
            "Os dados enviados são inválidos. Verifique os campos."
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> tratarCorpoInvalido(
        HttpMessageNotReadableException exception
    ) {
        return resposta(
            HttpStatus.BAD_REQUEST,
            "requisicao_invalida",
            "Não foi possível interpretar os dados enviados."
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> tratarErroInesperado(
            Exception exception
    ) {

        // Preserva as decisões de autorização.
        if (exception instanceof AccessDeniedException) {
            return resposta(
                    HttpStatus.FORBIDDEN,
                    "acesso_negado",
                    "Você não tem permissão para realizar esta operação."
            );
        }

        if (exception instanceof AuthenticationException) {
            return resposta(
                    HttpStatus.UNAUTHORIZED,
                    "autenticacao_invalida",
                    "Não foi possível autenticar a requisição."
            );
        }

        // Preserva status definidos pelo próprio Spring MVC.
        if (exception instanceof ErrorResponse erroSpring) {
            return respostaStatus(
                    erroSpring.getStatusCode(),
                    exception
            );
        }

        // Preserva status declarados em exceções anotadas.
        ResponseStatus anotacao =
                AnnotatedElementUtils.findMergedAnnotation(
                        exception.getClass(),
                        ResponseStatus.class
                );

        if (anotacao != null) {
            return respostaStatus(anotacao.code(), exception);
        }

        logger.error(
                "Falha interna não tratada na API.",
                exception
        );

        return resposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "erro_interno",
                "Ocorreu uma falha no servidor. Tente novamente mais tarde."
        );
    }

    private ResponseEntity<ApiErrorResponse> respostaStatus(
            HttpStatusCode status,
            Exception exception
    ) {

        if (status.is5xxServerError()) {
            logger.error(
                    "Falha HTTP {} na API.",
                    status.value(),
                    exception
            );
        }

        String mensagem = switch (status.value()) {
            case 400 -> "Os dados enviados são inválidos.";
            case 401 -> "Não foi possível autenticar a requisição.";
            case 403 -> "Você não tem permissão para esta operação.";
            case 404 -> "O recurso solicitado não foi encontrado.";
            case 405 -> "Esta operação não é permitida.";
            case 409 -> "A operação não pôde ser concluída devido a um conflito.";
            case 410 -> "O recurso solicitado não está mais disponível.";
            case 422 -> "Os dados não atendem às regras da operação.";
            default -> status.is5xxServerError()
                    ? "Ocorreu uma falha no servidor. Tente novamente mais tarde."
                    : "Não foi possível concluir a operação.";
        };

        return ResponseEntity
                .status(status)
                .body(
                        ApiErrorResponse.criar(
                                status,
                                "http_" + status.value(),
                                mensagem
                        )
                );
    }

    private ResponseEntity<ApiErrorResponse> resposta(
        HttpStatus status,
        String codigo,
        String mensagem
    ) {
        return ResponseEntity
            .status(status)
            .body(
                ApiErrorResponse.criar(
                    status,
                    codigo,
                    mensagem
                )
            );
    }
}
