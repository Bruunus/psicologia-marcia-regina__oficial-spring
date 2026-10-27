package br.com.psicologia.marcia.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
     * Gera um token com a duração configurada em jwt.expiracao,
     * expressa em milissegundos.
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
     * Usa a mesma codificação na geração e na validação.
     */
    private Key chaveAssinatura() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }
}