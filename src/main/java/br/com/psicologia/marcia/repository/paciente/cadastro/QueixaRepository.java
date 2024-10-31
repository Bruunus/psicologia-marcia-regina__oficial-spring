package br.com.psicologia.marcia.repository.paciente.cadastro;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.psicologia.marcia.model.Queixa;

public interface QueixaRepository extends JpaRepository <Queixa, Long> {

}
