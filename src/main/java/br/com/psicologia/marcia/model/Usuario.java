package br.com.psicologia.marcia.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class Usuario implements UserDetails {

	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	
	@NotNull
	private String login;
	@NotNull
	private String senha;

	@Enumerated(EnumType.STRING)
	private Role role;
	
	@Column(name = "cpf", nullable = false)
	private String cpf;


	
//	@OneToOne(mappedBy = "usuario")
//	private GerenciadorDeAcessoDeUsuario accessUserManager;
	
	
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getSenha() {
		return senha;
	}
	
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public Role getRole() {
		return role;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}
	
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	
	/**
	 * Retorna as permissões (authorities) do usuário com base no perfil (role) armazenado.
	 * O Spring Security exige que cada autoridade seja precedida por "ROLE_".
	 * 
	 * Perfis existentes até o momento: usuario e admin
	 *
	 * @return Collection com uma única autoridade do tipo ROLE_USER ou ROLE_ADMIN
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
	}

	
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return senha;
	}
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return login;
	}
	
	
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
    
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return "Login: "+this.login+" "+"Senha: "+this.senha;
    }

	
	
	
	
}
