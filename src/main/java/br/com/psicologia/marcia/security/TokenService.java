package br.com.psicologia.marcia.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
 
/**
 * Serviço responsável por gerar e validar tokens JWT.
 */
@Service
public class TokenService {


	@Value(value = "${api.security.token.secret}")
	private String secret;
	

    @Value("${jwt.expiracao}")
    private long expiracaoEmMillis;

    /**
     * Gera um token JWT contendo o login como subject e tempo de expiração.
     *
     * @param login o login do usuário autenticado
     * @return o token JWT gerado
     */
    public String gerarToken(String login) {
    	
        Date agora = new Date();
//        Date expiracao = new Date(agora.getTime() + expiracaoEmMillis);

        // Converte a chave secreta em bytes com codificação segura UTF-8
        Key chaveAssinatura = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));


        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(agora)
                .setExpiration(Date.from(dataExpiracao()))
                .signWith(chaveAssinatura, SignatureAlgorithm.HS256)
                .compact();
    }
	
	/**
     * Calcula a data de expiração do token com base em 2 horas a partir do horário atual.
     *
     * @return Instant - Data e hora de expiração do token
     */
	private Instant dataExpiracao() {
		return LocalDateTime.now()
				.plusHours(2)
				.toInstant(ZoneOffset.of("-03:00"));
	}

	
	/**
     * Extrai o subject (login) de um token JWT e valida sua assinatura e expiração.
     *
     * @param tokenJWT o token recebido no cabeçalho Authorization
     * @return o login do usuário (subject)
     * @throws RuntimeException se o token for inválido ou estiver expirado
     */
    public String getSubject(String tokenJWT) {
        try {
            Key chaveAssinatura = Keys.hmacShaKeyFor(secret.getBytes());

            return Jwts
                    .parserBuilder()
                    .setSigningKey(chaveAssinatura)
                    .build()
                    .parseClaimsJws(tokenJWT)
                    .getBody()
                    .getSubject();

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new RuntimeException("Token expirado!");
        } catch (io.jsonwebtoken.JwtException e) {
            throw new RuntimeException("Token inválido!");
        }
    }

    
    
  
    
	
	
}
