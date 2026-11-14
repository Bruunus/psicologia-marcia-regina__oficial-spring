package br.com.psicologia.marcia.service.usuario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import br.com.psicologia.marcia.security.TokenStore;

@Service
public class InvalidacaoSessaoUsuarioService {

    private final AuditoriaDeSessaoDeUsuarioService auditoriaService;
    private final TransactionTemplate transacaoAuditoria;

    public InvalidacaoSessaoUsuarioService(
            AuditoriaDeSessaoDeUsuarioService auditoriaService,
            PlatformTransactionManager transactionManager) {

        this.auditoriaService = auditoriaService;

        this.transacaoAuditoria = new TransactionTemplate(transactionManager);
        this.transacaoAuditoria.setPropagationBehavior(
                TransactionDefinition.PROPAGATION_REQUIRES_NEW
        );
    }

    /**
     * Captura a sessão atual e agenda sua invalidação após o commit.
     * Deve ser chamado dentro de uma transação de escrita.
     */
    public void agendarInvalidacaoAposCommit(String login, String motivo) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                    "A invalidação exige uma transação ativa."
            );
        }

        if (login == null || login.isBlank()
                || motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "Login e motivo são obrigatórios."
            );
        }

        synchronized (TokenStore.class) {
            TokenStore.SessaoRegistrada sessao =
                    TokenStore.obterSessaoPorLogin(login);

            if (sessao == null) {
                return;
            }

            String tokenCapturado = sessao.token();
            Long auditoriaIdCapturada = sessao.auditoriaId();

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            try {
                                invalidarSessao(
                                        login,
                                        tokenCapturado,
                                        auditoriaIdCapturada,
                                        motivo
                                );
                            } catch (RuntimeException e) {
                                System.err.println(
                                        "Falha ao registrar auditoria de "
                                        + "invalidação: "
                                        + e.getClass().getSimpleName()
                                );
                            }
                        }
                    }
            );
        }
    }

    /**
     * Invalida exclusivamente a sessão capturada anteriormente.
     * A auditoria usa uma nova transação após o commit principal.
     */
    public void invalidarSessao(
            String login,
            String token,
            Long auditoriaId,
            String motivo) {

        if (login == null || token == null || auditoriaId == null) {
            return;
        }

        synchronized (TokenStore.class) {
            TokenStore.SessaoRegistrada sessao =
                    TokenStore.obterSessao(login, token);

            if (sessao == null || !auditoriaId.equals(sessao.auditoriaId())) {
                return;
            }

            try {
                transacaoAuditoria.executeWithoutResult(status ->
                        auditoriaService.registrarLogoutDaSessao(
                                auditoriaId,
                                login,
                                motivo
                        )
                );
            } finally {
                // Mesmo que a auditoria falhe, o JWT antigo é invalidado.
                TokenStore.removerToken(login, token);
            }
        }
    }
}
