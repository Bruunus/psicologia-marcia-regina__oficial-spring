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
 * Serviço responsável por registrar e consultar sessões de usuários no sistema.
 * Armazena data/hora de login e logout para fins de auditoria e rastreamento.
 */
@Service
public class AuditoriaDeSessaoDeUsuarioService {
	
	@Autowired
    private AuditoriaDeSessaoDeUsuarioRepository auditoriaRepo;

    /**
     * Cria um novo registro de sessão para o usuário
     */
    public void registrarLogin(String login, HttpServletRequest request) {
        AuditoriaDeSessaoDeUsuario auditoria = new AuditoriaDeSessaoDeUsuario();
        
        String userAgent = request.getHeader("User-Agent");
        String ip = capturarIpCliente(request);
        auditoria.setLogin(login);
        auditoria.setStatus("ATIVA");
        auditoria.setDataLogin(LocalDateTime.now());
        auditoria.setIp(ip);        
        auditoria.setUserAgent(userAgent);
        
        auditoriaRepo.save(auditoria);
    }
    
    
    /**
     * Captura o IP real do cliente a partir da requisição HTTP.
     * Funciona corretamente mesmo atrás de proxies, balanceadores e no ambiente local.
     *
     * @param request A requisição HTTP.
     * @return O IP real do cliente, formatado corretamente.
     */
    public String capturarIpCliente(HttpServletRequest request) {
        // Lista de cabeçalhos comuns usados por proxies para repassar o IP original
        String[] headers = {
            "X-Forwarded-For",           // padrão de proxies
            "Proxy-Client-IP",           // Apache
            "WL-Proxy-Client-IP",        // WebLogic
            "HTTP_X_FORWARDED_FOR",      // proxies diversos
            "HTTP_X_FORWARDED",          // variantes
            "HTTP_X_CLUSTER_CLIENT_IP",  // AWS ELB
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Se o cabeçalho contiver vários IPs, pega o primeiro (cliente real)
                return ip.split(",")[0].trim();
            }
        }

        // Fallback: IP direto da requisição
        String ip = request.getRemoteAddr();
        

        // Conversão de loopback IPv6 para IPv4 (localhost)
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    
    
    /**
     * Encerra a última sessão ativa do usuário
     */
    @Transactional
    public void registrarLogout(String login, String motivo) {
        auditoriaRepo.encerrarSessaoAtiva(login, LocalDateTime.now(), motivo);
    }

    /**
     * Verifica se o usuário já possui uma sessão ativa
     */
    public boolean usuarioJaLogado(String login) {
        return auditoriaRepo.existsByLoginAndStatus(login, "ATIVA");
    }

    /**
     * Lista todas as sessões do usuário, ordenadas da mais recente à mais antiga
     */
    public List<AuditoriaDeSessaoDeUsuario> listarSessoesDoUsuario(String login) {
        return auditoriaRepo.findAllByLoginOrderByDataLoginDesc(login);
    }

}
