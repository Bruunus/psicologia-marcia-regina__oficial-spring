package br.com.psicologia.marcia.repository.usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;

import br.com.psicologia.marcia.model.ConviteUsuario;
import br.com.psicologia.marcia.model.Usuario;

@Repository
public interface ConviteUsuarioRepository extends JpaRepository<ConviteUsuario, Long> {

    Optional<ConviteUsuario> findByToken(String token);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ConviteUsuario c WHERE c.token = :token")
    Optional<ConviteUsuario> findByTokenComBloqueio(
            @Param("token") String token
    );

    List<ConviteUsuario> findByUsuarioAndUtilizadoFalse(Usuario usuario);

}