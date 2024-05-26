package br.com.bank.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {

	
	@NotBlank(message = "Nome é obrigatório")
	private String nome;
	@NotBlank(message = "Número da conta é obrigatório")
	private String numeroConta;
	@Positive
	private double saldo;
}
