package br.com.bank.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bank.exception.ClienteException;
import br.com.bank.model.Cliente;
import br.com.bank.model.ClienteDTO;
import br.com.bank.service.ClienteService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@PostMapping
	public ResponseEntity<Object> cadastrarCliente(@Valid @RequestBody ClienteDTO clienteDTO){
		try {
			clienteService.cadastrarCliente(clienteDTO);
			return ResponseEntity.status(201).body("Cliente cadastrado com sucesso");
		} catch (Exception e) {
			return ResponseEntity.status(404).body("Erro ao cadastrar cliente: " + e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<List<Cliente>> listarClientes(){
		List<Cliente> clientes = clienteService.listarClientes();
		return ResponseEntity.ok(clientes);
	}

	@GetMapping("/{numeroConta}")
	public ResponseEntity<Cliente> buscarNumeroConta(@PathVariable String numeroConta) throws ClienteException {
		Optional<Cliente> obterCliente = clienteService.buscarNumeroConta(numeroConta);
		return obterCliente.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}
}
