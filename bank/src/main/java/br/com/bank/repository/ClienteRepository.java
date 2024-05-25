package br.com.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.bank.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer>{

	Optional<Cliente> findByNumeroConta(String numeroConta);
}
