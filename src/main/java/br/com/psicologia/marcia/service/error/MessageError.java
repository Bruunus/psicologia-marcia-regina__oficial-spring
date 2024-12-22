package br.com.psicologia.marcia.service.error;

import org.springframework.stereotype.Service;

import br.com.psicologia.marcia.DTO.autenticacao.AccessUserManagerRecord;

@Service
public class MessageError {

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	public AccessUserManagerRecord fromStringToRecord(String login) {
        return new AccessUserManagerRecord(login);
    }
}
