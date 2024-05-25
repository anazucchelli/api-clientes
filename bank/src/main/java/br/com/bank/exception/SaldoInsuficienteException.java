package br.com.bank.exception;

public class SaldoInsuficienteException extends TransferenciaException{
	public SaldoInsuficienteException(String message) {
        super(message);
    }	
}
