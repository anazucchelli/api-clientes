package br.com.bank.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bank.exception.SaldoInsuficienteException;
import br.com.bank.exception.TransferenciaException;
import br.com.bank.model.Cliente;
import br.com.bank.model.Transferencia;
import br.com.bank.model.TransferenciaDTO;
import br.com.bank.repository.ClienteRepository;
import br.com.bank.repository.TransferenciaRepository;

@Service
public class TransferenciaService {

	private static final Logger logger = LoggerFactory.getLogger(TransferenciaService.class);

	@Autowired
	private TransferenciaRepository transferenciaRepository;
	@Autowired
	private ClienteService clienteService;
	@Autowired
	private ClienteRepository clienteRepository;

	private final Lock lock = new ReentrantLock();

	@Transactional
	public Transferencia realizarTransferencia(TransferenciaDTO transferenciaDTO) throws TransferenciaException {
		lock.lock();
		try {
			logger.info("Iniciando transferência de {} para {}", transferenciaDTO.getContaOrigem(),
					transferenciaDTO.getContaDestino());

			Optional<Cliente> origemOpt = clienteRepository.findByNumeroConta(transferenciaDTO.getContaOrigem());
			Cliente origem = origemOpt.orElseThrow(() -> new TransferenciaException("Conta de origem não encontrada"));

			Optional<Cliente> destinoOpt = clienteRepository.findByNumeroConta(transferenciaDTO.getContaDestino());
			Cliente destino = destinoOpt
					.orElseThrow(() -> new TransferenciaException("Conta de destino não encontrada"));
			
			if (transferenciaDTO.getValor() > 100.00) {
				throw new TransferenciaException("Valor acima do limite permitido");
			}

			if (origem.getSaldo() < transferenciaDTO.getValor()) {
				throw new SaldoInsuficienteException("Saldo insuficiente");
			}
			
			// realiza a transferencia
			origem.setSaldo(origem.getSaldo() - transferenciaDTO.getValor());
			destino.setSaldo(destino.getSaldo() + transferenciaDTO.getValor());
			// atualiza os dados
			clienteRepository.save(origem);
			clienteRepository.save(destino);

			Transferencia transferencia = new Transferencia();
			transferencia.setContaOrigem(origem);
			transferencia.setContaDestino(destino);
			transferencia.setValor(transferenciaDTO.getValor());
			transferencia.setData(new Date());
			transferencia.setSucesso(true);
			transferenciaRepository.save(transferencia);

			logger.info("Transferência concluída");
			return transferencia;
		} finally {
			lock.unlock();
		}
	}

	public List<Transferencia> buscarTransferencias(String numeroConta) throws TransferenciaException {
		Optional<Cliente> clienteOpt = clienteService.buscarNumeroConta(numeroConta);
		if (clienteOpt.isPresent()) {
			Cliente cliente = clienteOpt.get();
			return transferenciaRepository.findByContaOrigemOrContaDestinoOrderByDataDesc(cliente, cliente);
		} else {
			throw new TransferenciaException("Nenhuma transferência encontrada");
		}
	}
}
