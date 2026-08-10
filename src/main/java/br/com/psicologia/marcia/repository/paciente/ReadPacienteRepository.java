package br.com.psicologia.marcia.repository.paciente;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.psicologia.marcia.model.Paciente;


public interface ReadPacienteRepository extends JpaRepository <Paciente, Long> {

	//List<Paciente> findAll();

	List<Paciente> findAllByOrderByNomeCompletoAsc();

    List<Paciente> findByNomeCompletoContainingIgnoreCaseOrderByNomeCompletoAsc(
            String nomePaciente
    );
	
	@Query(value = "SELECT COUNT(*) > 0 FROM paciente WHERE cpf = :cpf", nativeQuery = true)
	Integer buscaDePacienteDuplicado(@Param("cpf")String cpf);
	
	/**
	 * Caregamento dos dados do paciente para a tela 'Identificação'
	 * @param cpf
	 * @return
	 */
	Paciente findByCpf(String cpf);
	
	 

	

	 
	
}
