package br.com.bank.model;

import lombok.Data;

@Data
public class TransferenciaDTO {

	private String contaOrigem;
	private String contaDestino;
    private double valor;
}
