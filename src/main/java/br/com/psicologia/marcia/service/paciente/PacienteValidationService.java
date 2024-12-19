package br.com.psicologia.marcia.service.paciente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.repository.paciente.ReadPacienteRepository;
/**
 * 
 * @author Bruno Fernandes dos Santos - Desenvolvedor Java Fullstack
 *
 */
@Service
public class PacienteValidationService  {

	@Autowired
	private ReadPacienteRepository readPacienteRepository;
	
	
	/**
	 * Método de serviço de validação de paciente. Este método valida os dados vindos da request consultando
	 * no banco de dados se os mesmos já contém registro, a busca é feita realizando um COUNT SQL para varrer 
	 * todos os registros, se a condição for acima de zero então a validação falha e emite o status code 400.
	 * @param nomeCompleto
	 * @param cpf
	 * @return Status code como resultado final da validação
	 */
	protected Integer validacaoNaoPodeSerIgual(String cpf) {
		
		Integer pacienteCount = 
				readPacienteRepository.buscaDePacienteDuplicado(cpf);
		
		if(pacienteCount > 0) {
			System.out.println("Paciente já cadastrado no banco de dados");
			return 400;
		} else {
			System.out.println("Liberado da validação para cadastro");
			return 200;
		}		
	}
	
	
	
	
}
