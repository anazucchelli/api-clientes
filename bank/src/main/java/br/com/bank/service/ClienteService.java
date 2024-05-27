package br.com.bank.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bank.exception.ClienteException;
import br.com.bank.model.Cliente;
import br.com.bank.model.ClienteDTO;
import br.com.bank.repository.ClienteRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	public Cliente cadastrarCliente(ClienteDTO clienteDTO) throws ClienteException {
		if (clienteRepository.findByNumeroConta(clienteDTO.getNumeroConta()).isPresent()) {
			throw new ClienteException("Número de conta ja existente: " + clienteDTO.getNumeroConta());
		}
		Cliente cliente = Cliente.builder().nome(clienteDTO.getNome()).numeroConta(clienteDTO.getNumeroConta())
				.saldo(clienteDTO.getSaldo()).build();
		return clienteRepository.save(cliente);

	}

	public List<Cliente> listarClientes() throws ClienteException {
		if (clienteRepository.findAll().isEmpty()) {
			throw new ClienteException("Lista de clientes está vazia");
		}
		return clienteRepository.findAll();
	}

	public Optional<Cliente> buscarNumeroConta(String numeroConta) {
		Optional<Cliente> cliente = clienteRepository.findByNumeroConta(numeroConta);
		cliente.orElseThrow(() -> new ClienteException("Conta não encontrada"));
		return cliente;
		}
}
