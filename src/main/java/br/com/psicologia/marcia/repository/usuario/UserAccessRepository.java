package br.com.psicologia.marcia.repository.usuario;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.model.AccessUserManager;

@Repository
public interface UserAccessRepository extends JpaRepository<AccessUserManager, Long> {

	
//	@Query(value = "SELECT * FROM reserva r INNER JOIN data d ON r.id = d.reserva_id WHERE MONTH(d.data_retirada) = :mes AND status = 'FINALIZADA'", nativeQuery = true)
//	List<Reserva> pesquisaPorMesAtualFinalizadas(@Param("mes") int mes);
	
	@Query(value = "SELECT status_login FROM gerenciador_de_acesso_de_usuario WHERE nome = :usuarioLogin", nativeQuery = true)
	Boolean statusLoginUsuario(@Param("usuarioLogin") String usuarioLogin);

	AccessUserManager findByNome(String usuarioLogin);

	
	@Transactional
    @Modifying
    @Query(value = "UPDATE gerenciador_de_acesso_de_usuario SET status_login = :status_login,  ultimo_login = :ultimo_login "
    		+ "WHERE nome = :nome ", nativeQuery = true)
	void updateAcessoDeUsuario(
			@Param("nome") String nome, 
			@Param("status_login") Boolean status_login, 
			@Param("ultimo_login") LocalDateTime ultimo_login
			);

	
	@Transactional
    @Modifying
    @Query(value = "UPDATE gerenciador_de_acesso_de_usuario SET status_login = :status_login,  ultimo_logout = :ultimo_logout "
    		+ "WHERE nome = :nome ", nativeQuery = true)
	void updateLogoffDeUsuario(
			@Param("nome") String nome, 
			@Param("status_login") Boolean status_login, 
			@Param("ultimo_logout") LocalDateTime ultimo_logout
			);
	 

 

}
