package br.com.psicologia.marcia.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.psicologia.marcia.model.Analitics;
import br.com.psicologia.marcia.model.Usuario;
import br.com.psicologia.marcia.service.analytics.RequisicaoAnaliticaService;
import br.com.psicologia.marcia.service.analytics.SuporteAnalytics;
import br.com.psicologia.marcia.service.usuario.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;

class JwtAuthenticationFilterFalhaInternaTest {

    private UsuarioService usuarioService;
    private RequisicaoAnaliticaService analyticsService;
    private JwtAuthenticationFilter filtro;

    private String login;
    private String token;

    @BeforeEach
    void preparar() {
        SecurityContextHolder.clearContext();

        TokenService tokenService = new TokenService();

        // Chave exclusiva deste teste. Não utiliza segredos da aplicação.
        ReflectionTestUtils.setField(
                tokenService,
                "secret",
                "chave-exclusiva-de-teste-jwt-0123456789-ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        );

        ReflectionTestUtils.setField(
                tokenService,
                "expiracaoEmMillis",
                7200000L
        );

        usuarioService = mock(UsuarioService.class);
        analyticsService = mock(RequisicaoAnaliticaService.class);
        SuporteAnalytics suporteAnalytics = mock(SuporteAnalytics.class);

        filtro = new JwtAuthenticationFilter(
                tokenService,
                usuarioService,
                analyticsService,
                suporteAnalytics
        );

        login = "teste-" + UUID.randomUUID();
        token = tokenService.gerarToken(login);

        TokenStore.adicionarToken(login, token, 1L);
    }

    @AfterEach
    void limpar() {
        if (login != null && token != null) {
            TokenStore.removerToken(login, token);
        }

        SecurityContextHolder.clearContext();
    }

    @Test
    void deveRetornar500EPreservarSessaoAposRecuperacao() throws Exception {
        Usuario usuario = mock(Usuario.class);

        when(usuario.getUsername()).thenReturn(login);
        when(usuario.getAuthorities()).thenReturn(List.of());

        /*
         * Primeira consulta: falha de infraestrutura.
         * Segunda consulta: serviço recuperado.
         */
        when(usuarioService.loadUserByUsername(login))
                .thenThrow(new DataAccessResourceFailureException(
                        "Falha simulada de conexão com o banco"
                ))
                .thenReturn(usuario);

        // 1. Simula a falha interna.
        MockHttpServletResponse respostaFalha =
                new MockHttpServletResponse();

        FilterChain cadeiaFalha = mock(FilterChain.class);

        filtro.doFilter(
                criarRequisicao(),
                respostaFalha,
                cadeiaFalha
        );

        assertEquals(500, respostaFalha.getStatus());

        assertEquals(
                "Falha interna ao validar a sessão.",
                respostaFalha.getContentAsString(StandardCharsets.UTF_8)
        );

        assertEquals(
                StandardCharsets.UTF_8.name(),
                respostaFalha.getCharacterEncoding()
        );

        assertFalse(
                respostaFalha.getContentAsString()
                        .contains("Falha simulada")
        );

        // A requisição com falha não deve chegar ao endpoint.
        verifyNoInteractions(cadeiaFalha);
        verifyNoInteractions(analyticsService);

        // O erro interno não pode apagar a sessão.
        assertTrue(TokenStore.tokenValido(login, token));
        assertEquals(
                Long.valueOf(1L),
                TokenStore.obterSessao(login, token).auditoriaId()
        );

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        // 2. Repete a requisição com o MESMO token após a recuperação.
        MockHttpServletResponse respostaRecuperacao =
                new MockHttpServletResponse();

        FilterChain cadeiaRecuperacao = mock(FilterChain.class);

        doAnswer(invocacao -> {
            var autenticacao =
                    SecurityContextHolder.getContext().getAuthentication();

            assertNotNull(autenticacao);
            assertTrue(autenticacao.isAuthenticated());
            assertSame(usuario, autenticacao.getPrincipal());

            HttpServletResponse resposta = invocacao.getArgument(1);
            resposta.setStatus(HttpServletResponse.SC_OK);

            return null;
        }).when(cadeiaRecuperacao).doFilter(any(), any());

        filtro.doFilter(
                criarRequisicao(),
                respostaRecuperacao,
                cadeiaRecuperacao
        );

        assertEquals(200, respostaRecuperacao.getStatus());
        assertTrue(TokenStore.tokenValido(login, token));

        verify(cadeiaRecuperacao, times(1)).doFilter(any(), any());
        verify(usuarioService, times(2)).loadUserByUsername(login);
        verify(analyticsService, times(1)).salvar(any(Analitics.class));
    }

    private MockHttpServletRequest criarRequisicao() {
        MockHttpServletRequest request = new MockHttpServletRequest(
                "POST",
                "/api/auth/validar-sessao"
        );

        request.addHeader("Authorization", "Bearer " + token);

        return request;
    }
}