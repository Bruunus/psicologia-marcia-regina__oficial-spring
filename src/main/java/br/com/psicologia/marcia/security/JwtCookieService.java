package br.com.psicologia.marcia.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class JwtCookieService {

    private final String nomeCookie;
    private final boolean secure;
    private final String sameSite;
    private final String path;
    private final long expiracaoMs;

    public JwtCookieService(
            @Value("${psicologia.marcia.auth.cookie-name:psico_auth}")
            String nomeCookie,

            @Value("${psicologia.marcia.auth.cookie-secure:false}")
            boolean secure,

            @Value("${psicologia.marcia.auth.cookie-same-site:Lax}")
            String sameSite,

            @Value("${psicologia.marcia.auth.cookie-path:/}")
            String path,

            @Value("${jwt.expiracao}")
            long expiracaoMs
    ) {
        this.nomeCookie = nomeCookie;
        this.secure = secure;
        this.sameSite = sameSite;
        this.path = path;
        this.expiracaoMs = expiracaoMs;
    }

    public String montarCookieRemocao() {
        return ResponseCookie
                .from(nomeCookie, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(path)
                .maxAge(Duration.ZERO)
                .build()
                .toString();
    }

    public String lerToken(jakarta.servlet.http.HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
            if (nomeCookie.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    public String montarCookie(String token) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "JWT ausente para emissao do cookie."
            );
        }

        if (expiracaoMs <= 0) {
            throw new IllegalStateException(
                    "Tempo de expiracao JWT invalido."
            );
        }

        if (nomeCookie.startsWith("__Host-")
                && (!secure || !"/".equals(path))) {
            throw new IllegalStateException(
                    "Cookie __Host- exige Secure e Path=/."
            );
        }

        return ResponseCookie
                .from(nomeCookie, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(path)
                .maxAge(Duration.ofMillis(expiracaoMs))
                .build()
                .toString();
    }
}
