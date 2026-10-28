package br.com.psicologia.marcia.service.usuario;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.model.AuditoriaDeSessaoDeUsuario;
import br.com.psicologia.marcia.repository.usuario.AuditoriaDeSessaoDeUsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Registra e consulta sessões de usuários para auditoria.
 */
@Service
public class AuditoriaDeSessaoDeUsuarioService {

    @Autowired
    private AuditoriaDeSessaoDeUsuarioRepository auditoriaRepo;

    /**
     * Reconcilia auditorias anteriores e registra o novo login.
     *
     * Deve ser chamado após a verificação de sessão no TokenStore,
     * dentro do bloqueio utilizado pelo controller de login.
     *
     * O encerramento anterior e a criação da nova auditoria
     * participam da mesma transação.
     */
    @Transactional
    public Long registrarLogin(String login, HttpServletRequest request) {
        LocalDateTime agora = LocalDateTime.now();

        /*
         * O fluxo de login já confirmou que não existe uma sessão
         * válida registrada no TokenStore para este usuário.
         *
         * Encerra registros que ficaram ATIVA no banco, sem atribuir
         * uma causa de logout que não foi comprovada.
         */
        auditoriaRepo.encerrarSessaoAtiva(
                login,
                agora,
                "RECONCILIACAO_NO_LOGIN"
        );

        AuditoriaDeSessaoDeUsuario auditoria =
                new AuditoriaDeSessaoDeUsuario();

        auditoria.setLogin(login);
        auditoria.setStatus("ATIVA");
        auditoria.setDataLogin(agora);
        auditoria.setIp(capturarIpCliente(request));
        auditoria.setUserAgent(request.getHeader("User-Agent"));

        return auditoriaRepo.save(auditoria).getId();
    }

    /**
     * Captura o IP do cliente usando os cabeçalhos existentes
     * ou, na ausência deles, o endereço remoto da requisição.
     */
    public String capturarIpCliente(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);

            if (ip != null
                    && !ip.isEmpty()
                    && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        String ip = request.getRemoteAddr();

        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }

        return ip;
    }

    /**
     * Mantido para compatibilidade com chamadas existentes.
     * Encerra todas as sessões ativas do login.
     */
    @Transactional
    public void registrarLogout(String login, String motivo) {
        auditoriaRepo.encerrarSessaoAtiva(
                login,
                LocalDateTime.now(),
                motivo
        );
    }

    /**
     * Encerra somente a auditoria da sessão informada.
     * Não modifica sessões já encerradas ou de outro usuário.
     *
     * @return true se a sessão foi encerrada nesta chamada;
     *         false se nenhum registro foi atualizado.
     */
    @Transactional
    public boolean registrarLogoutDaSessao(
            Long sessaoId,
            String login,
            String motivo) {

        if (sessaoId == null || login == null || login.isBlank()) {
            throw new IllegalArgumentException(
                    "Sessão e login são obrigatórios para registrar o logout."
            );
        }

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "O motivo do logout é obrigatório."
            );
        }

        return auditoriaRepo.encerrarSessaoPorId(
                sessaoId,
                login,
                LocalDateTime.now(),
                motivo
        ) == 1;
    }

    /**
     * Verifica registros marcados como ativos na auditoria.
     * Não substitui a validação do token e do TokenStore.
     */
    public boolean usuarioJaLogado(String login) {
        return auditoriaRepo.existsByLoginAndStatus(login, "ATIVA");
    }

    /**
     * Lista as sessões, da mais recente à mais antiga.
     */
    public List<AuditoriaDeSessaoDeUsuario> listarSessoesDoUsuario(
            String login) {
        return auditoriaRepo.findAllByLoginOrderByDataLoginDesc(login);
    }
}