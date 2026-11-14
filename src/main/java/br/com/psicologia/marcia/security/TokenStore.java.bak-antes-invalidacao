package br.com.psicologia.marcia.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Armazena as sessões registradas durante a execução da aplicação.
 */
@Component
public class TokenStore {

    private static final Map<String, SessaoRegistrada> sessoes =
            new ConcurrentHashMap<>();

    @Autowired
    private TokenService tokenService;

    /**
     * Token e auditoria pertencentes à mesma sessão.
     */
    public record SessaoRegistrada(String token, Long auditoriaId) {
    }

    /**
     * Mantido para compatibilidade com chamadas existentes.
     * O fluxo de login será atualizado para informar a auditoria.
     */
    public static void adicionarToken(String login, String token) {
        sessoes.put(login, new SessaoRegistrada(token, null));
    }

    /**
     * Registra o token associado ao ID da auditoria de login.
     */
    public static void adicionarToken(
            String login,
            String token,
            Long auditoriaId) {

        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login obrigatório.");
        }

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token obrigatório.");
        }

        if (auditoriaId == null) {
            throw new IllegalArgumentException(
                    "ID da auditoria obrigatório."
            );
        }

        sessoes.put(
                login,
                new SessaoRegistrada(token, auditoriaId)
        );
    }

    /**
     * Mantido para compatibilidade com chamadas existentes.
     * Remove a sessão independentemente do token registrado.
     */
    public static void removerToken(String login) {
        sessoes.remove(login);
    }

    /**
     * Remove somente a sessão correspondente ao token recebido.
     */
    public static boolean removerToken(String login, String token) {
        SessaoRegistrada sessao = obterSessao(login, token);

        if (sessao == null) {
            return false;
        }

        return sessoes.remove(login, sessao);
    }

    /**
     * Recupera a sessão somente se o token recebido corresponder
     * ao token registrado para o usuário.
     *
     * Não valida assinatura ou expiração.
     */
    public static SessaoRegistrada obterSessao(
            String login,
            String token) {

        if (login == null || token == null) {
            return null;
        }

        SessaoRegistrada sessao = sessoes.get(login);

        if (sessao == null || !token.equals(sessao.token())) {
            return null;
        }

        return sessao;
    }

    /**
     * Confere a correspondência com o token registrado.
     * Assinatura e expiração são verificadas pelo TokenService.
     */
    public static boolean tokenValido(String login, String token) {
        return obterSessao(login, token) != null;
    }

    /**
     * Verifica se existe uma sessão com token válido para o usuário.
     */
    public boolean usuarioJaLogado(String login) {
        SessaoRegistrada sessao = sessoes.get(login);

        if (sessao == null) {
            return false;
        }

        try {
            tokenService.getSubject(sessao.token());
            return true;

        } catch (RuntimeException e) {
            // Remove apenas a sessão que foi verificada.
            // Uma sessão diferente registrada nesse intervalo é preservada.
            sessoes.remove(login, sessao);
            return false;
        }
    }
}