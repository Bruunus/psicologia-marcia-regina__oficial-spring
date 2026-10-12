package br.com.psicologia.marcia.repository.paciente;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.model.enums.StatusPaciente;

public interface ReadPacienteRepository
        extends JpaRepository<Paciente, Long> {

    List<Paciente> findAllByOrderByNomeCompletoAsc();

    List<Paciente>
            findByNomeCompletoContainingIgnoreCaseOrderByNomeCompletoAsc(
                    String nomePaciente
            );

    List<Paciente>
            findByNomeCompletoContainingIgnoreCaseAndStatusPacienteOrderByNomeCompletoAsc(
                    String nomePaciente,
                    StatusPaciente statusPaciente
            );

    Optional<Paciente> findByIdAndStatusPaciente(
            Long pacienteId,
            StatusPaciente statusPaciente
    );

    @Query(
        value = """
            SELECT COUNT(*) > 0
            FROM paciente
            WHERE cpf = :cpf
        """,
        nativeQuery = true
    )
    Integer buscaDePacienteDuplicado(
            @Param("cpf") String cpf
    );

    /**
     * Carregamento dos dados do paciente para a tela Identificação.
     *
     * @param cpf CPF do paciente
     * @return paciente encontrado
     */
    Paciente findByCpf(String cpf);
}