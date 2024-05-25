package br.com.bank.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bank.exception.BusinessException;
import br.com.bank.model.Cliente;
import br.com.bank.repository.ClienteRepository;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	public Cliente cadastrarCliente(Cliente cliente) {
		return clienteRepository.save(cliente);
	}
	
	public List<Cliente> listarClientes(){
		return clienteRepository.findAll();
	}
	
	public Optional<Cliente> buscarNumeroConta(String numeroConta) {
		System.out.println("Buscando cliente com numero da conta: " + numeroConta);
		Optional<Cliente> cliente = clienteRepository.findByNumeroConta(numeroConta);
		System.out.println("cliente encontrado: " + cliente);
		return cliente;
    }
}
