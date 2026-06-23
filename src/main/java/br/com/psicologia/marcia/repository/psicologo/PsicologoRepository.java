package br.com.psicologia.marcia.repository.psicologo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.psicologia.marcia.model.Psicologo;

public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {

    Optional<Psicologo> findFirstByAtivoTrueOrderByIdAsc();
}