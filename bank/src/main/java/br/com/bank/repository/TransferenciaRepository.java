package br.com.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.bank.model.Cliente;
import br.com.bank.model.Transferencia;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Integer>{
	List<Transferencia> findByContaOrigemOrContaDestinoOrderByDataDesc(Cliente contaOrigem, Cliente contaDestino);
}
