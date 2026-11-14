package br.com.psicologia.marcia.controller.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import br.com.psicologia.marcia.model.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.psicologia.marcia.DTO.autenticacao.DadosTokenJWT;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.security.JwtCookieService;
import br.com.psicologia.marcia.security.TokenService;
import br.com.psicologia.marcia.security.TokenStore;
import br.com.psicologia.marcia.service.usuario.AuditoriaDeSessaoDeUsuarioService;
import br.com.psicologia.marcia.service.usuario.UsuarioService;
import br.com.psicologia.marcia.service.usuario.UsuarioService.MotivoLogout;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class ControllerLogin {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private JwtCookieService jwtCookieService;

    @Autowired
    private AuditoriaDeSessaoDeUsuarioService auditoriaService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Corpo opcional da solicitação de logout.
     */
    public record DadosLogout(String motivo) {
    }

    public record RespostaSessao(String login, Role role, long expiraEm) {
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody Usuario dados,
            HttpServletRequest request) {

        try {
            if (usuarioService.validacaoDeLogin(dados.getLogin())) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("usuario_inexistente");
            }

            var autenticacaoToken =
                    new UsernamePasswordAuthenticationToken(
                            dados.getLogin(),
                            dados.getSenha()
                    );

            var autenticacao = manager.authenticate(autenticacaoToken);

            String login =
                    ((UserDetails) autenticacao.getPrincipal()).getUsername();

            var perfil =
                    ((Usuario) autenticacao.getPrincipal()).getRole();

            /*
             * Impede que duas chamadas deste endpoint executem
             * simultaneamente a verificação e o registro da sessão
             * nesta instância da aplicação.
             */
            synchronized (TokenStore.class) {
                if (tokenStore.usuarioJaLogado(login)) {
                    return ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body("usuario_ja_logado");
                }

                String token = tokenService.gerarToken(login);
                String cookieJwt = jwtCookieService.montarCookie(token);

                // Primeiro persiste a auditoria e obtém seu ID.
                Long auditoriaId =
                        auditoriaService.registrarLogin(login, request);

                // Registra o token junto à auditoria correspondente.
                TokenStore.adicionarToken(login, token, auditoriaId);

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookieJwt)
                        .header(HttpHeaders.CACHE_CONTROL, "no-store")
                        .body(new RespostaSessao(login, perfil, tokenService.getExpiracaoEmMs(token)));
            }

        } catch (DisabledException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("usuario_inativo_ou_pendente");

        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("senha_incorreta");

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao processar autenticação.");
        }
    }

    /**
     * Confirma que a requisição passou pela validação do filtro JWT.
     *
     * O filtro verifica assinatura, expiração e correspondência
     * com a sessão registrada no TokenStore.
     *
     * Não renova o token nem altera o prazo de inatividade.
     */
    @PostMapping("/validar-sessao")
    public ResponseEntity<RespostaSessao> validarSessao(
            HttpServletRequest request, Authentication authentication) {
        String token = jwtCookieService.lerToken(request);
        if (token == null || authentication == null
                || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok()
                .header("Cache-Control", "no-store")
                .body(new RespostaSessao(usuario.getLogin(), usuario.getRole(),
                        tokenService.getExpiracaoEmMs(token)));
    }

    /**
     * Solicita o encerramento da sessão correspondente ao token.
     *
     * Sem corpo ou sem motivo: logout manual.
     * Motivos aceitos: LOGOUT_MANUAL e INATIVIDADE.
     * A expiração é identificada pelo serviço, não pelo cliente.
     */
    @PostMapping("/deslogar")
    public ResponseEntity<String> logout(
            HttpServletRequest request,
            @RequestBody(required = false) DadosLogout dados) {

        String token = jwtCookieService.lerToken(request);
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(org.springframework.http.HttpHeaders.SET_COOKIE,
                            jwtCookieService.montarCookieRemocao())
                    .body("Cookie de sessão ausente.");
        }

        MotivoLogout motivo = MotivoLogout.LOGOUT_MANUAL;

        if (dados != null && dados.motivo() != null) {
            try {
                motivo = MotivoLogout.valueOf(dados.motivo());

            } catch (IllegalArgumentException e) {
                return ResponseEntity
                        .badRequest()
                        .body(
                                "Motivo de logout não permitido. "
                                + "Use LOGOUT_MANUAL ou INATIVIDADE."
                        );
            }
        }

        String resultado = usuarioService.logout(token, motivo);

        if (resultado.contains("sucesso")) {
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.SET_COOKIE,
                            jwtCookieService.montarCookieRemocao()
                    )
                    .header(HttpHeaders.CACHE_CONTROL, "no-store")
                    .body(resultado);
        }

        if (resultado.contains("inválido")
                || resultado.contains("expirado")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(
                            HttpHeaders.SET_COOKIE,
                            jwtCookieService.montarCookieRemocao()
                    )
                    .body(resultado);
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(resultado);
    }
}