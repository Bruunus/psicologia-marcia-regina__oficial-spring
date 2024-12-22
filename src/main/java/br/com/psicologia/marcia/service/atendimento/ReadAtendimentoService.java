package br.com.psicologia.marcia.service.atendimento;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.DTO.atendimento.AtendimentoRecord;
import br.com.psicologia.marcia.DTO.atendimento.ConsultaDTOCarregamentoTelaHome;
import br.com.psicologia.marcia.model.Paciente;
import br.com.psicologia.marcia.repository.atendimento.AtendimentoRepository;

@Service
public class ReadAtendimentoService {

	@Autowired
	private AtendimentoRepository atendimentoRepository;
	
	
	public List<ConsultaDTOCarregamentoTelaHome> carregarTelaHome() {		
		List<ConsultaDTOCarregamentoTelaHome> list = atendimentoRepository.carregarListaHome();		
		return list;
	}


	
}
