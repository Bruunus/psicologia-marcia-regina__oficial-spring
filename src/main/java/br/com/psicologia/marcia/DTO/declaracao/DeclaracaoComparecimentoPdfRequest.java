
package br.com.psicologia.marcia.DTO.declaracao;

import jakarta.validation.constraints.NotNull;

public record DeclaracaoComparecimentoPdfRequest(

    @NotNull(message = "O ID da declaração é obrigatório")
    Long declaracaoId

) {
}
