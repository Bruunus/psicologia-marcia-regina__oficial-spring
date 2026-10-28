package br.com.psicologia.marcia.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Serviço responsável por gerar e validar tokens JWT.
 */
@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${jwt.expiracao}")
    private long expiracaoEmMillis;

    /**
     * Gera um token com identificador próprio e duração configurada
     * em jwt.expiracao, expressa em milissegundos.
     */
    public String gerarToken(String login) {
        if (expiracaoEmMillis <= 0) {
            throw new IllegalStateException(
                    "jwt.expiracao deve ser maior que zero."
            );
        }

        Instant agora = Instant.now();
        Instant expiracao = agora.plusMillis(expiracaoEmMillis);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(login)
                .setIssuedAt(Date.from(agora))
                .setExpiration(Date.from(expiracao))
                .signWith(chaveAssinatura(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida a assinatura e a expiração antes de retornar o login.
     * Tokens expirados continuam proibidos nas rotas protegidas.
     */
    public String getSubject(String tokenJWT) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(chaveAssinatura())
                    .build()
                    .parseClaimsJws(tokenJWT)
                    .getBody()
                    .getSubject();

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expirado!", e);

        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Token inválido!", e);
        }
    }

    /**
     * Obtém os dados necessários para encerrar uma sessão.
     *
     * Aceita expiração somente neste fluxo. A assinatura continua
     * sendo validada pelo parser.
     *
     * Não utilizar este método para autenticar requisições.
     * A correspondência com a sessão registrada no TokenStore
     * deve ser verificada antes de remover o token.
     */
    public DadosTokenLogout getDadosParaLogout(String tokenJWT) {
        validarEstruturaAssinada(tokenJWT);

        Claims claims;
        boolean expirado;

        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(chaveAssinatura())
                    .build()
                    .parseClaimsJws(tokenJWT)
                    .getBody();

            expirado = false;

        } catch (ExpiredJwtException e) {
            // Recupera os dados do token cuja assinatura foi verificada,
            // mas cujo prazo de validade terminou.
            claims = e.getClaims();
            expirado = true;

        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Token inválido!", e);
        }

        String login = claims.getSubject();

        if (login == null || login.isBlank()
                || claims.getExpiration() == null) {
            throw new IllegalArgumentException(
                    "Token inválido: login ou expiração ausente."
            );
        }

        return new DadosTokenLogout(login, expirado);
    }

    /**
     * Exige as três partes de um JWT assinado, incluindo a assinatura.
     */
    private void validarEstruturaAssinada(String tokenJWT) {
        if (tokenJWT == null || tokenJWT.isBlank()) {
            throw new IllegalArgumentException("Token inválido!");
        }

        String[] partes = tokenJWT.split("\\.", -1);

        if (partes.length != 3
                || partes[0].isBlank()
                || partes[1].isBlank()
                || partes[2].isBlank()) {
            throw new IllegalArgumentException("Token inválido!");
        }
    }

    /**
     * Usa a mesma codificação na geração e na validação.
     */
    private Key chaveAssinatura() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Dados exclusivos do processamento de logout.
     */
    public record DadosTokenLogout(String login, boolean expirado) {
    }
}