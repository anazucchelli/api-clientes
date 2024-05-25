package br.com.bank.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.bank.exception.BusinessException;
import br.com.bank.exception.TransferenciaException;
import br.com.bank.model.Cliente;
import br.com.bank.model.Transferencia;
import br.com.bank.model.TransferenciaDTO;
import br.com.bank.service.TransferenciaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transferencias")
public class TransferenciaController {

	@Autowired
	private TransferenciaService transferenciaService;

	@PostMapping
	public ResponseEntity<Object> realizarTransferencia(@Valid @RequestBody TransferenciaDTO transferenciaDTO)
			throws TransferenciaException {
		try {
			transferenciaService.realizarTransferencia(transferenciaDTO);
			return ResponseEntity.ok().body("Transferencia realizada com sucesso");
		} catch (BusinessException e) {
			return ResponseEntity.badRequest().body("Erro ao realizar transferencia: " + e.getMessage());
		} 
	}

	@GetMapping("/{numeroConta}")
	public ResponseEntity<Object> buscarTransferencias(@PathVariable String numeroConta) throws TransferenciaException{
		 try {
		        List<Transferencia> transferencias = transferenciaService.buscarTransferencias(numeroConta);
		        if (transferencias.isEmpty()) {
		            return ResponseEntity.status(404).body("Nenhuma transferência encontrada para a conta: " + numeroConta);
		        }
		        return ResponseEntity.ok(transferencias);
		    } catch (Exception e) {
		        return ResponseEntity.status(500).body("Erro ao buscar transferências: " + e.getMessage());
		    }
	}

}
