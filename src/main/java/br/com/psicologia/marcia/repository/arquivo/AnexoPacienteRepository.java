
package br.com.psicologia.marcia.repository.arquivo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.psicologia.marcia.model.AnexoPaciente;

@Repository
public interface AnexoPacienteRepository
        extends JpaRepository<AnexoPaciente, Long> {

    /*
     * Lista todos os anexos de um paciente.
     *
     * Ordena pelos mais recentes primeiro.
     */
    List<AnexoPaciente>
        findByPaciente_IdOrderByDataAnexoDescIdDesc(
            Long pacienteId
        );

    /*
     * Lista os anexos de um paciente
     * pertencentes a uma pasta especifica.
     *
     * Exemplos de pasta:
     * QUEIXAS
     * RELATORIOS
     * FINANCEIRO
     * LAUDOS
     * OUTROS_ARQUIVOS
     */
    List<AnexoPaciente>
        findByPaciente_IdAndPastaOrderByDataAnexoDescIdDesc(
            Long pacienteId,
            String pasta
        );

    /*
     * Busca um anexo especifico vinculado
     * ao paciente informado.
     *
     * Util para validar o relacionamento
     * antes de disponibilizar o documento.
     */
    Optional<AnexoPaciente>
        findByIdAndPaciente_Id(
            Long id,
            Long pacienteId
        );

}
