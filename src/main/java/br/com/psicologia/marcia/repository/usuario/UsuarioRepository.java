package br.com.psicologia.marcia.repository.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import br.com.psicologia.marcia.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	UserDetails findBylogin(String login);
	
	
	/**
	 * Procura o nome de um usuário no banco de dados.
	 * Usado para verificar a existência de um login específico.
	 *
	 * @param usuarioLogin o nome de login do usuário
	 * @return o nome do usuário, se encontrado; {@code null} caso contrário
	 */
	@Query(value = "SELECT nome FROM gerenciador_de_acesso_de_usuario WHERE nome = :usuarioLogin", nativeQuery = true)
	String procurarPorNome(@Param("usuarioLogin") String usuarioLogin);

	 

}
