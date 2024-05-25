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

import br.com.bank.exception.BusinessException;
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
	public Transferencia realizarTransferencia(TransferenciaDTO transferenciaDTO) throws BusinessException {
		lock.lock();
		try {
			logger.info("Iniciando transferência de {} para {}", transferenciaDTO.getContaOrigem(), transferenciaDTO.getContaDestino());

			Optional<Cliente> origemOpt = clienteService.buscarNumeroConta(transferenciaDTO.getContaOrigem());
			Cliente origem = origemOpt.orElseThrow(() -> new BusinessException("Conta de origem não encontrada"));

			Optional<Cliente> destinoOpt = clienteService.buscarNumeroConta(transferenciaDTO.getContaDestino());
			Cliente destino = destinoOpt.orElseThrow(() -> new BusinessException("Conta de destino não encontrada"));

				if (origem.getSaldo() < transferenciaDTO.getValor()) {
					throw new BusinessException("Saldo insuficiente");
				}
				if (transferenciaDTO.getValor() > 100.00) {
					throw new BusinessException("Valor acima do limite permitido");
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

	public List<Transferencia> buscarTransferencias(String numeroConta) throws BusinessException {
		Optional<Cliente> clienteOpt = clienteService.buscarNumeroConta(numeroConta);
		if (clienteOpt.isPresent()) {
			Cliente cliente = clienteOpt.get();
			return transferenciaRepository.findByContaOrigemOrContaDestinoOrderByDataDesc(cliente, cliente);
		}
		return null;
	}
}
