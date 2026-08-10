package br.com.psicologia.marcia.repository.relatorio.psicologico;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.psicologia.marcia.model.RelatorioPsicologico;

public interface RelatorioPsicologicoRepository
        extends JpaRepository<RelatorioPsicologico, Long> {

    List<RelatorioPsicologico>
            findByPacienteIdOrderByDataCriacaoDesc(Long pacienteId);

    Long countByPacienteId(Long pacienteId);
}