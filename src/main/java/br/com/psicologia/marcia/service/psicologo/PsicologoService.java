package br.com.psicologia.marcia.service.psicologo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.psicologia.marcia.model.Psicologo;
import br.com.psicologia.marcia.repository.psicologo.PsicologoRepository;

@Service
public class PsicologoService {

    private final PsicologoRepository psicologoRepository;

    public PsicologoService(PsicologoRepository psicologoRepository) {
        this.psicologoRepository = psicologoRepository;
    }

    @Transactional(readOnly = true)
    public Psicologo buscarPsicologoAtivoPrincipal() {
        return psicologoRepository
                .findFirstByAtivoTrueOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Nenhum psicólogo ativo encontrado."));
    }
}