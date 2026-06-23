package br.com.psicologia.marcia.DTO.acompanhamento;

import java.time.LocalDateTime;

import br.com.psicologia.marcia.model.enums.TipoPapelDocumento;
import jakarta.validation.constraints.NotNull;

public class EmitirHistoricoAtendimentoRequestDTO {

    @NotNull(message = "O id do paciente é obrigatório")
    private Long pacienteId;

    @NotNull(message = "A data do acompanhamento é obrigatória")
    private LocalDateTime dataAcompanhamento;

    @NotNull(message = "O tipo de papel é obrigatório")
    private TipoPapelDocumento tipoPapel;

    public EmitirHistoricoAtendimentoRequestDTO() {
    }

    public EmitirHistoricoAtendimentoRequestDTO(
            Long pacienteId,
            LocalDateTime dataAcompanhamento,
            TipoPapelDocumento tipoPapel
    ) {
        this.pacienteId = pacienteId;
        this.dataAcompanhamento = dataAcompanhamento;
        this.tipoPapel = tipoPapel;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public LocalDateTime getDataAcompanhamento() {
        return dataAcompanhamento;
    }

    public void setDataAcompanhamento(LocalDateTime dataAcompanhamento) {
        this.dataAcompanhamento = dataAcompanhamento;
    }

    public TipoPapelDocumento getTipoPapel() {
        return tipoPapel;
    }

    public void setTipoPapel(TipoPapelDocumento tipoPapel) {
        this.tipoPapel = tipoPapel;
    }
}