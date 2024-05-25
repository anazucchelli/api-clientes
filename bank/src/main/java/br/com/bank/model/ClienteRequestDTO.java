package br.com.bank.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ClienteRequestDTO {
	
	@NotBlank(message = "Nome é obrigatório")
	private String nome;
	@NotBlank(message = "Numero da Conta é obrigatório")
	private String numeroConta;
	@Positive
	private double saldo;

}
