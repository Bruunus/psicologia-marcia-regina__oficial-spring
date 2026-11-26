package br.com.psicologia.marcia.service.atendimento;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class AtendimentoHistoricoService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public int atualizarPacientesAntigos() {

        return entityManager.createQuery("""
            UPDATE Paciente p
            SET p.atendimentoIniciado = true
            WHERE p.atendimentoIniciado = false
            AND (
                EXISTS (
                    SELECT a.id FROM Atendimento a
                    WHERE a.paciente.id = p.id
                    AND a.dataUltimoAtendimento IS NOT NULL
                )
                OR EXISTS (
                    SELECT a.id FROM AcompanhamentoPaciente a
                    WHERE a.paciente.id = p.id
                )
                OR EXISTS (
                    SELECT r.id FROM RelatorioPsicologico r
                    WHERE r.paciente.id = p.id
                )
                OR EXISTS (
                    SELECT d.id FROM DeclaracaoComparecimento d
                    WHERE d.paciente.id = p.id
                )
                OR EXISTS (
                    SELECT l.id FROM Laudo l
                    WHERE l.paciente.id = p.id
                )
                OR EXISTS (
                    SELECT f.id FROM PagamentoPaciente f
                    WHERE f.paciente.id = p.id
                )
                OR EXISTS (
                    SELECT g.id FROM Agendamento g
                    WHERE g.paciente.id = p.id
                )
                OR EXISTS (
                    SELECT n.id FROM AnexoPaciente n
                    WHERE n.paciente.id = p.id
                )
            )
            """).executeUpdate();
    }
}
