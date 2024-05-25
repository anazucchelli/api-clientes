package br.com.bank.model;

import lombok.Data;

@Data
public class ClienteResponseDTO {
	
	private String nome;
	private String numeroConta;
	private double saldo;
}
