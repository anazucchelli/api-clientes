package br.com.bank.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferenciaDTO {

	@NotBlank(message = "Conta Origem é obrigatório")
	private String contaOrigem;
	@NotBlank(message = "Conta Destino é obrigatório")
	private String contaDestino;
	@Positive
    private double valor;
}
