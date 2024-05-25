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
import br.com.bank.repository.TransferenciaRepository;

@Service
public class TransferenciaService {

	private static final Logger logger = LoggerFactory.getLogger(TransferenciaService.class);

	@Autowired
	private TransferenciaRepository transferenciaRepository;
	@Autowired
	private ClienteService clienteService;
	
	private final Lock lock = new ReentrantLock();

	@Transactional
	public Transferencia realizarTransferencia(String contaOrigem, String contaDestino, Double valor) throws BusinessException {
		lock.lock();
		try {
			logger.info("Iniciando transferência de {} para {}", contaOrigem, contaDestino);
			System.out.println("Iniciando transferência de " + valor + " de " + contaOrigem + " para " + contaDestino);
			Optional<Cliente> origemOpt = clienteService.buscarNumeroConta(contaOrigem);
			Cliente origem = origemOpt.orElseThrow(() -> new BusinessException("Conta de origem não encontrada"));

			Optional<Cliente> destinoOpt = clienteService.buscarNumeroConta(contaDestino);
			Cliente destino = destinoOpt.orElseThrow(() -> new BusinessException("Conta de destino não encontrada"));

				if (origem.getSaldo() < valor) {
					throw new BusinessException("Saldo insuficiente");
				}
				if (valor > 100.00) {
					throw new BusinessException("Valor acima do limite permitido");
				}
				// realiza a transferencia
				origem.setSaldo(origem.getSaldo() - valor);
				destino.setSaldo(destino.getSaldo() + valor);
				// atualiza os dados
				clienteService.cadastrarCliente(origem);
				clienteService.cadastrarCliente(destino);

				Transferencia transferencia = new Transferencia();
				transferencia.setContaOrigem(origem);
				transferencia.setContaDestino(destino);
				transferencia.setValor(valor);
				transferencia.setData(new Date());
				transferencia.setSucesso(true);
				transferenciaRepository.save(transferencia);

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
