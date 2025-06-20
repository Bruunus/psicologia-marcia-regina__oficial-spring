package br.com.psicologia.marcia.repository.usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.model.AuditoriaDeSessaoDeUsuario;

@Repository
public interface AuditoriaDeSessaoDeUsuarioRepository extends JpaRepository<AuditoriaDeSessaoDeUsuario, Long> {

	/**
     * Retorna a última sessão ativa do usuário (status = 'ATIVA').
     */
    Optional<AuditoriaDeSessaoDeUsuario> findTopByLoginAndStatusOrderByDataLoginDesc(String login, String status);

    /**
     * Retorna todas as sessões de um usuário.
     */
    List<AuditoriaDeSessaoDeUsuario> findAllByLoginOrderByDataLoginDesc(String login);

    /**
     * Atualiza os dados de logout da última sessão ativa.
     */
    @Modifying
    @Transactional
    @Query("""
        UPDATE AuditoriaDeSessaoDeUsuario s 
        SET s.dataLogout = :logoutTime, s.status = 'ENCERRADA', s.motivoLogout = :motivo 
        WHERE s.login = :login AND s.status = 'ATIVA'
    """)
    void encerrarSessaoAtiva(
        @Param("login") String login,
        @Param("logoutTime") LocalDateTime logoutTime,
        @Param("motivo") String motivo
    );

    /**
     * Verifica se o usuário possui uma sessão ativa.
     */
    boolean existsByLoginAndStatus(String login, String status);
}

