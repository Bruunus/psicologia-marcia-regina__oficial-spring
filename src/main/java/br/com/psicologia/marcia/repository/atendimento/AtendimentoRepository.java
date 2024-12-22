package br.com.psicologia.marcia.repository.atendimento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.psicologia.marcia.DTO.atendimento.ConsultaDTOCarregamentoTelaHome;
import br.com.psicologia.marcia.model.Atendimento;

@Repository
public interface AtendimentoRepository extends JpaRepository <Atendimento, Long> {

	
	@Query("SELECT new br.com.psicologia.marcia.DTO.atendimento.ConsultaDTOCarregamentoTelaHome("
		      + "ate.dataUltimoAtendimento, pac.id, pac.nomeCompleto, pac.cpf, pac.perfil) " +
		      "FROM Atendimento ate JOIN ate.paciente pac")
		List<ConsultaDTOCarregamentoTelaHome> carregarListaHome();
	
	
 
}
