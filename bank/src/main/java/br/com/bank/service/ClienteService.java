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
	
	public Cliente cadastrarCliente(Cliente cliente) throws BusinessException {
		if (clienteRepository.findByNumeroConta(cliente.getNumeroConta()).isPresent()) {
            throw new BusinessException("Número de conta ja existente: " + cliente.getNumeroConta());
        }
        return clienteRepository.save(cliente);
    }
	
	public List<Cliente> listarClientes(){
		return clienteRepository.findAll();
	}
	
	public Optional<Cliente> buscarNumeroConta(String numeroConta) {
		Optional<Cliente> cliente = clienteRepository.findByNumeroConta(numeroConta);
		return cliente;
    }
}
