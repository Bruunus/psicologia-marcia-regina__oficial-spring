package br.com.psicologia.marcia.repository.paciente;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.psicologia.marcia.model.Paciente;

 
public interface ReadPacienteRepository extends JpaRepository <Paciente, Long> {

	List<Paciente> findAll();
	
}
