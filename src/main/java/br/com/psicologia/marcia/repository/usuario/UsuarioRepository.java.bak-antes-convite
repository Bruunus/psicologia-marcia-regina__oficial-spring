package br.com.psicologia.marcia.repository.usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import br.com.psicologia.marcia.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	UserDetails findBylogin(String login);
	
	boolean existsByLogin(String login);


	/**
     * Busca um usuário com base no ID e login.
     *
     * @param id    ID do usuário.
     * @param login Login do usuário.
     * @return Um Optional com o usuário, se encontrado.
     */
	Optional<Usuario> findByIdAndLogin(Long id, String login);

	
	Optional<Usuario> findByLoginAndId(String login, Long id);

	 

}
