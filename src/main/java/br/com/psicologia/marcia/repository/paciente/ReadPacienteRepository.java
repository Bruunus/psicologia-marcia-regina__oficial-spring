package br.com.psicologia.marcia.repository.paciente;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.psicologia.marcia.model.Paciente;

@Repository
public interface ReadPacienteRepository extends JpaRepository <Paciente, Long> {

	List<Paciente> findAll();
	
	@Query(value = "SELECT COUNT(*) > 0 FROM paciente WHERE cpf = :cpf", nativeQuery = true)
	Integer buscaDePacienteDuplicado(@Param("cpf")String cpf);
	
	

	

	 
	
}
