package br.com.psicologia.marcia.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "atendimento")
public class Atendimento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private LocalDate dataUltimoAtendimento;	
	
	
	@ManyToOne
	@JoinColumn(name = "paciente_id", nullable = false)
	private Paciente paciente;

	
	
	
	
	public LocalDate getDataUltimoAtendimento() {
		return dataUltimoAtendimento;
	}
	
	public void setDataUltimoAtendimento(LocalDate dataUltimoAtendimento) {
		this.dataUltimoAtendimento = dataUltimoAtendimento;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}
	
	
	

}
