package br.com.psicologia.marcia.repository.paciente;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.psicologia.marcia.model.Paciente;

public interface UpdatePacienteRepository extends JpaRepository <Paciente, Long>  {

}
