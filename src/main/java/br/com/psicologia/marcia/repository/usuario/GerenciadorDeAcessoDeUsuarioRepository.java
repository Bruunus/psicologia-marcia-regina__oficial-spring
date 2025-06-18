package br.com.psicologia.marcia.repository.usuario;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.model.GerenciadorDeAcessoDeUsuario;

@Repository
public interface GerenciadorDeAcessoDeUsuarioRepository extends JpaRepository<GerenciadorDeAcessoDeUsuario, Long> {

 
	
	/**
	 * Verifica o status de login do usuário com base no nome informado.
	 *
	 * @param usuarioLogin o nome de login do usuário
	 * @return {@code true} se o usuário estiver com status de login ativo, {@code false} caso contrário
	 */
	@Query(value = "SELECT status_login FROM gerenciador_de_acesso_de_usuario WHERE nome = :usuarioLogin", nativeQuery = true)
	Boolean statusLoginUsuario(@Param("usuarioLogin") String usuarioLogin);

	
	
	/**
	 * Busca um registro completo do usuário no banco de dados com base no nome.
	 *
	 * @param usuarioLogin o nome de login do usuário
	 * @return o objeto {@link GerenciadorDeAcessoDeUsuario} correspondente ao nome informado
	 */
	GerenciadorDeAcessoDeUsuario findByNome(String usuarioLogin);
	
	
	
	/**
	 * Procura o nome de um usuário no banco de dados.
	 * Usado para verificar a existência de um login específico.
	 *
	 * @param usuarioLogin o nome de login do usuário
	 * @return o nome do usuário, se encontrado; {@code null} caso contrário
	 */
	@Query(value = "SELECT nome FROM gerenciador_de_acesso_de_usuario WHERE nome = :usuarioLogin", nativeQuery = true)
	String procurarPorNome(@Param("usuarioLogin") String usuarioLogin);
	
	 

	/**
	 * Atualiza os dados de acesso do usuário no momento do login.
	 * Define o status de login como {@code true} e registra o horário do último login.
	 *
	 * @param nome o nome do usuário
	 * @param status_login o novo status de login (normalmente {@code true})
	 * @param ultimo_login o horário em que o login foi realizado
	 */
	@Transactional
    @Modifying
    @Query(value = "UPDATE gerenciador_de_acesso_de_usuario SET status_login = :status_login,  ultimo_login = :ultimo_login "
    		+ "WHERE nome = :nome ", nativeQuery = true)
	void updateAcessoDeUsuario(
			@Param("nome") String nome, 
			@Param("status_login") Boolean status_login, 
			@Param("ultimo_login") LocalDateTime ultimo_login
			);

	
	
	/**
	 * Atualiza os dados de acesso do usuário no momento do logout.
	 * Define o status de login como {@code false} e registra o horário do logout.
	 *
	 * @param nome o nome do usuário
	 * @param status_login o novo status de login (normalmente {@code false})
	 * @param ultimo_logout o horário em que o logout foi realizado
	 */
	@Transactional
    @Modifying
    @Query(value = "UPDATE gerenciador_de_acesso_de_usuario SET status_login = :status_login,  ultimo_logout = :ultimo_logout "
    		+ "WHERE nome = :nome ", nativeQuery = true)
	void updateLogoffDeUsuario(
			@Param("nome") String nome, 
			@Param("status_login") Boolean status_login, 
			@Param("ultimo_logout") LocalDateTime ultimo_logout
			);
	 
	
	/**
	 * Verifica se existe um usuário com o nome informado e com o status de login igual ao 
	 * especificado.
	 *
	 * @param nome o nome do usuário
	 * @param status_login o status de login esperado ({@code true} ou {@code false})
	 * @return {@code true} se o usuário com o status especificado existir; {@code false} caso contrário
	 */
//	boolean existsByNomeAndStatusLogin(String nome);

	


	/**
	 * Atualiza o campo {@code statusLogin} para {@code false} no registro do usuário
	 * identificado pelo nome informado. Essa operação é utilizada para realizar o logout
	 * forçado do usuário, invalidando seu status de login no banco de dados.
	 *
	 * @param nome o nome (login) do usuário que será deslogado
	 */
	@Modifying
	@Query(value = "UPDATE gerenciador_de_acesso_de_usuario SET status_login = false WHERE nome = :nome", nativeQuery = true)
	void updateStatusLoginByNome(@Param("nome") String nome);



 

}
