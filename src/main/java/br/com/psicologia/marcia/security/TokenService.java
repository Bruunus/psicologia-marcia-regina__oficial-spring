package br.com.psicologia.marcia.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.psicologia.marcia.model.Usuario;
 
/**
 * Serviço responsável por gerar e validar tokens JWT.
 */
@Service
public class TokenService {


	@Value(value = "${api.security.token.secret}")
	private String secret;

	/**
     * Gera um token JWT assinado com os dados do usuário autenticado.
     *
     * @param usuario - O usuário autenticado
     * @return String - Token JWT gerado
     */	
	public String gerarToken(Usuario usuario) {
		try {
		    Algorithm algoritimo = Algorithm.HMAC256(secret);
		    return JWT.create()     
		        .withIssuer("Teste de autenticação - Bruno Fernandes")		// identificação da aplicação
		        .withSubject(usuario.getUsername())							//  usuário que recebe o token 
		        .withClaim("prontuário", usuario.getId())					// identificação do usuario -  por exemplo "id" 
		        .withExpiresAt(dataExpiracao())								// Declaração de validade do token
		        .sign(algoritimo);											// assinatura do token que será o algoritmo
		} catch (JWTCreationException exception){
		    throw new RuntimeException("Erro ao gerar JWT" , exception);
		}
	 
		
	}
	
	
	/**
     * Calcula a data de expiração do token com base em 2 horas a partir do horário atual.
     *
     * @return Instant - Data e hora de expiração do token
     */
	private Instant dataExpiracao() {
		return LocalDateTime.now()
				.plusMinutes(30)
				.toInstant(ZoneOffset.of("-03:00"));
	}
	
	
	/**
     * Extrai o login (subject) do token JWT.
     *
     * @param token O token JWT
     * @return String Login do usuário contido no token
     */
    public String getSubject(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                .withIssuer("Teste de autenticação - Bruno Fernandes")
                .build()
                .verify(token)
                .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!", exception);
        }
    }
	
	
}
