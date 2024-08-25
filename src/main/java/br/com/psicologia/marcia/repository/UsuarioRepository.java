package br.com.psicologia.marcia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import br.com.psicologia.marcia.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	UserDetails findBylogin(String login);
	

	 

}
