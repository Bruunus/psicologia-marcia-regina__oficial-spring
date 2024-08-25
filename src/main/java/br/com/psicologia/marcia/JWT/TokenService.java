package br.com.psicologia.marcia.JWT;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import br.com.psicologia.marcia.model.Usuario;
 

@Service
public class TokenService {


	@Value(value = "${api.security.token.secret}")
	private String secret;

	/**
	 * Metodo para gerar token - A classe Algorithm serve para poder gerar um token 
	 * @return
	 */
	public String gerarToken(Usuario usuario) {
//		System.out.println(secret);
		try {
		    Algorithm algoritimo = Algorithm.HMAC256(secret);
		    return JWT.create()     
		        .withIssuer("Teste de autenticação - Bruno Fernandes")	// identificação da aplicação
		        .withSubject(usuario.getUsername())		//  usuário que recebe o token 
		        .withClaim("prontuário", usuario.getId())	// identificação do usuario -  por exemplo "id" 
		        .withClaim("", "")		// Voce pode chamar vários detalhes do usuario
		        .withExpiresAt(dataExpiracao())// Declaração de validade do token
		        .sign(algoritimo);		// assinatura do token que será o algoritmo
		} catch (JWTCreationException exception){
		    throw new RuntimeException("Erro ao gerar JWT" , exception);
		}
	 
		
	}
	
	private Instant dataExpiracao() {
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
	}
	
	
}
