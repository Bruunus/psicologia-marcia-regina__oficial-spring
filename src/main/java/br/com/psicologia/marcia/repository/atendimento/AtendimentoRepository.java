package br.com.psicologia.marcia.repository.atendimento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.psicologia.marcia.DTO.atendimento.ConsultaDTOCarregamentoTelaHome;
import br.com.psicologia.marcia.model.Atendimento;
import br.com.psicologia.marcia.model.enums.StatusPaciente;


public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    /**
     * Query responsável por trazer os dados relevantes personalizados do paciente para a
     * tela home principal
     *
     * @return lista de dados da tela home
     */
    @Query("""
        SELECT new br.com.psicologia.marcia.DTO.atendimento.ConsultaDTOCarregamentoTelaHome(
            ate.dataUltimoAtendimento,
            pac.id,
            pac.nomeCompleto,
            pac.cpf,
            pac.perfil,
            pac.idade
        )
        FROM Atendimento ate
        JOIN ate.paciente pac
        WHERE pac.statusPaciente = :statusPaciente
    """)
    List<ConsultaDTOCarregamentoTelaHome> carregarListaHome(
            @Param("statusPaciente") StatusPaciente statusPaciente
    );
}