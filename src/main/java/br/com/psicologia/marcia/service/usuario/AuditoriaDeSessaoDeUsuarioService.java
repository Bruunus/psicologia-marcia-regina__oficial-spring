package br.com.psicologia.marcia.service.usuario;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.model.AuditoriaDeSessaoDeUsuario;
import br.com.psicologia.marcia.repository.usuario.AuditoriaDeSessaoDeUsuarioRepository;

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
    public void registrarLogin(String login) {
        AuditoriaDeSessaoDeUsuario auditoria = new AuditoriaDeSessaoDeUsuario();
        auditoria.setLogin(login);
        auditoria.setStatus("ATIVA");
        auditoria.setDataLogin(LocalDateTime.now());
        auditoriaRepo.save(auditoria);
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
