package br.com.psicologia.marcia.service.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.model.Analitics;
import br.com.psicologia.marcia.repository.analytics.RequisicaoAnaliticaRepository;

/**
 * Serviço responsável pelo gerenciamento de requisições analíticas.
 */
@Service
public class RequisicaoAnaliticaService {

    @Autowired
    private RequisicaoAnaliticaRepository repository;

    /**
     * Persiste uma nova requisição analítica no banco de dados.
     *
     * @param analitics objeto com os dados da requisição
     */
    public void salvar(Analitics analitics) {
        repository.save(analitics);
    }
}