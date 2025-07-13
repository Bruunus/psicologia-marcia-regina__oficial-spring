package br.com.psicologia.marcia.security;



import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * Armazena tokens JWT válidos durante a sessão da aplicação.
 * Responsável por registrar e invalidar tokens de usuários logados.
 */
@Component
public class TokenStore {

    private static final Map<String, String> tokenMap = new ConcurrentHashMap<>();
    
    @Autowired
    private TokenService tokenService;

    /**
     * Registra um novo token JWT vinculado ao login do usuário.
     *
     * @param login O login do usuário.
     * @param token O token JWT gerado no login.
     */
    public static void adicionarToken(String login, String token) {
        tokenMap.put(login, token);
    }

    /**
     * Remove o token JWT associado ao login do usuário.
     *
     * @param login O login do usuário.
     */
    public static void removerToken(String login) {
        tokenMap.remove(login);
    }

    /**
     * Verifica se o token fornecido é o mesmo registrado para o usuário.
     *
     * @param login O login do usuário.
     * @param token O token JWT recebido na requisição.
     * @return true se o token for válido e estiver registrado; false caso contrário.
     */
    public static boolean tokenValido(String login, String token) {
        return token.equals(tokenMap.get(login));
    }

    /**
     * Verifica se o usuário já possui um token registrado.
     *
     * @param login O login do usuário.
     * @return true se o usuário já estiver logado (token presente), false caso contrário.
     */
    public  boolean usuarioJaLogado(String login) {
        String token = tokenMap.get(login);
        
        if (token == null) {
            return false;
        }
        
     // Verifica se o token ainda é válido
        try {
        	tokenService.getSubject(token);  // ou getSubject(token), se lançar exceção
            return true;
        } catch (RuntimeException e) {
            // Token expirado ou inválido → remove e retorna false
            tokenMap.remove(login);
            return false;
        }
        
    }
}
