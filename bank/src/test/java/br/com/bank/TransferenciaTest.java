package br.com.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.bank.exception.SaldoInsuficienteException;
import br.com.bank.exception.TransferenciaException;
import br.com.bank.model.Cliente;
import br.com.bank.model.Transferencia;
import br.com.bank.model.TransferenciaDTO;
import br.com.bank.repository.ClienteRepository;
import br.com.bank.repository.TransferenciaRepository;
import br.com.bank.service.TransferenciaService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TransferenciaTest {

    @InjectMocks
    private TransferenciaService transferenciaService;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private TransferenciaRepository transferenciaRepository;

    private Cliente contaOrigem;
    private Cliente contaDestino;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        contaOrigem = Cliente.builder().nome("Origem").numeroConta("123").saldo(1000.0).build();
        contaDestino = Cliente.builder().nome("Destino").numeroConta("456").saldo(500.0).build();
    }

    @Test
    public void testRealizarTransferencia_Sucesso() throws TransferenciaException {
        TransferenciaDTO transferenciaDTO = TransferenciaDTO.builder()
                .contaOrigem("123")
                .contaDestino("456")
                .valor(50.0)
                .build();

        when(clienteRepository.findByNumeroConta("123")).thenReturn(Optional.of(contaOrigem));
        when(clienteRepository.findByNumeroConta("456")).thenReturn(Optional.of(contaDestino));

        Transferencia transferencia = transferenciaService.realizarTransferencia(transferenciaDTO);

        assertNotNull(transferencia);
        assertEquals(contaOrigem, transferencia.getContaOrigem());
        assertEquals(contaDestino, transferencia.getContaDestino());
        assertEquals(50.0, transferencia.getValor());
        assertTrue(transferencia.getSucesso());

        verify(clienteRepository, times(1)).save(contaOrigem);
        verify(clienteRepository, times(1)).save(contaDestino);
        verify(transferenciaRepository, times(1)).save(transferencia);
    }

    @Test
    public void testRealizarTransferencia_SaldoInsuficiente() {
        TransferenciaDTO transferenciaDTO = TransferenciaDTO.builder()
                .contaOrigem("123")
                .contaDestino("456")
                .valor(1100.0)
                .build();

        when(clienteRepository.findByNumeroConta("123")).thenReturn(Optional.of(contaOrigem));
        when(clienteRepository.findByNumeroConta("456")).thenReturn(Optional.of(contaDestino));

        assertThrows(SaldoInsuficienteException.class, () -> {
            transferenciaService.realizarTransferencia(transferenciaDTO);
        });

        verify(clienteRepository, never()).save(contaOrigem);
        verify(clienteRepository, never()).save(contaDestino);
        verify(transferenciaRepository, never()).save(any(Transferencia.class));
    }

    @Test
    public void testRealizarTransferencia_ValorAcimaDoLimite() {
        TransferenciaDTO transferenciaDTO = TransferenciaDTO.builder()
                .contaOrigem("123")
                .contaDestino("456")
                .valor(150.0)
                .build();

        when(clienteRepository.findByNumeroConta("123")).thenReturn(Optional.of(contaOrigem));
        when(clienteRepository.findByNumeroConta("456")).thenReturn(Optional.of(contaDestino));

        assertThrows(TransferenciaException.class, () -> {
            transferenciaService.realizarTransferencia(transferenciaDTO);
        });

        verify(clienteRepository, never()).save(contaOrigem);
        verify(clienteRepository, never()).save(contaDestino);
        verify(transferenciaRepository, never()).save(any(Transferencia.class));
    }

    @Test
    public void testRealizarTransferencia_ContaOrigemNaoEncontrada() {
        TransferenciaDTO transferenciaDTO = TransferenciaDTO.builder()
                .contaOrigem("999")
                .contaDestino("456")
                .valor(100.0)
                .build();

        when(clienteRepository.findByNumeroConta("999")).thenReturn(Optional.empty());

        assertThrows(TransferenciaException.class, () -> {
            transferenciaService.realizarTransferencia(transferenciaDTO);
        });

        verify(clienteRepository, never()).save(contaOrigem);
        verify(clienteRepository, never()).save(contaDestino);
        verify(transferenciaRepository, never()).save(any(Transferencia.class));
    }

    @Test
    public void testRealizarTransferencia_ContaDestinoNaoEncontrada() {
        TransferenciaDTO transferenciaDTO = TransferenciaDTO.builder()
                .contaOrigem("123")
                .contaDestino("999")
                .valor(100.0)
                .build();

        when(clienteRepository.findByNumeroConta("123")).thenReturn(Optional.of(contaOrigem));
        when(clienteRepository.findByNumeroConta("999")).thenReturn(Optional.empty());

        assertThrows(TransferenciaException.class, () -> {
            transferenciaService.realizarTransferencia(transferenciaDTO);
        });

        verify(clienteRepository, never()).save(contaOrigem);
        verify(clienteRepository, never()).save(contaDestino);
        verify(transferenciaRepository, never()).save(any(Transferencia.class));
    }

    @Test
    public void testBuscarTransferencias_Sucesso() throws TransferenciaException {
        Transferencia transferencia1 = Transferencia.builder()
                .contaOrigem(contaOrigem)
                .contaDestino(contaDestino)
                .valor(100.0)
                .data(new Date())
                .sucesso(true)
                .build();

        Transferencia transferencia2 = Transferencia.builder()
                .contaOrigem(contaDestino)
                .contaDestino(contaOrigem)
                .valor(50.0)
                .data(new Date())
                .sucesso(true)
                .build();

        List<Transferencia> transferencias = Arrays.asList(transferencia1, transferencia2);

        when(clienteRepository.findByNumeroConta("123")).thenReturn(Optional.of(contaOrigem));
        when(transferenciaRepository.findByContaOrigemOrContaDestinoOrderByDataDesc(contaOrigem, contaOrigem)).thenReturn(transferencias);

        List<Transferencia> resultado = transferenciaService.buscarTransferencias("123");

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(transferencia1));
        assertTrue(resultado.contains(transferencia2));
    }

    @Test
    public void testBuscarTransferencias_ContaNaoEncontrada() {
        when(clienteRepository.findByNumeroConta("999")).thenReturn(Optional.empty());

        assertThrows(TransferenciaException.class, () -> {
            transferenciaService.buscarTransferencias("999");
        });
    }

    @Test
    public void testRealizarTransferenciaConcorrente() throws InterruptedException {
        TransferenciaDTO transferenciaDTO = TransferenciaDTO.builder()
                .contaOrigem("123")
                .contaDestino("456")
                .valor(50.0)
                .build();

        when(clienteRepository.findByNumeroConta("123")).thenReturn(Optional.of(contaOrigem));
        when(clienteRepository.findByNumeroConta("456")).thenReturn(Optional.of(contaDestino));

        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    transferenciaService.realizarTransferencia(transferenciaDTO);
                } catch (TransferenciaException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Verificar saldos após as transferências concorrentes
        Cliente contaOrigemAtualizada = clienteRepository.findByNumeroConta("123").orElseThrow();
        Cliente contaDestinoAtualizada = clienteRepository.findByNumeroConta("456").orElseThrow();

        assertEquals(1000.0 - (numberOfThreads * 50.0), contaOrigemAtualizada.getSaldo());
        assertEquals(500.0 + (numberOfThreads * 50.0), contaDestinoAtualizada.getSaldo());
    }
}
