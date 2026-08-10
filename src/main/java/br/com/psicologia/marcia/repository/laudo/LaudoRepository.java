package br.com.psicologia.marcia.repository.laudo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.psicologia.marcia.model.Laudo;

public interface LaudoRepository
    extends JpaRepository<Laudo, Long> {

    List<Laudo>
        findByPacienteIdAndStatusDeleteFalseOrderByDataCriacaoDesc(
            Long pacienteId
        );

    Optional<Laudo>
        findByIdAndStatusDeleteFalse(
            Long id
        );

    boolean
        existsByIdAndStatusDeleteFalse(
            Long id
        );
}