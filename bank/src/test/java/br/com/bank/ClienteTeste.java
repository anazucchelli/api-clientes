package br.com.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.bank.exception.ClienteException;
import br.com.bank.model.Cliente;
import br.com.bank.model.ClienteDTO;
import br.com.bank.repository.ClienteRepository;
import br.com.bank.service.ClienteService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ClienteTeste {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    @Test
    public void testCadastrarCliente() {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Teste");
        clienteDTO.setNumeroConta("789456");
        clienteDTO.setSaldo(200.0);

        when(clienteRepository.findByNumeroConta(clienteDTO.getNumeroConta())).thenReturn(Optional.empty());

        Cliente cliente = Cliente.builder().nome("Teste").numeroConta("789456").saldo(200.0).build();
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente result = clienteService.cadastrarCliente(clienteDTO);

        assertNotNull(result);
        assertEquals(clienteDTO.getNome(), result.getNome());
        assertEquals(clienteDTO.getNumeroConta(), result.getNumeroConta());
        assertEquals(clienteDTO.getSaldo(), result.getSaldo());

        verify(clienteRepository, times(1)).findByNumeroConta(clienteDTO.getNumeroConta());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    public void testCadastrarCliente_ThrowsBusinessException() {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("João");
        clienteDTO.setNumeroConta("110497");

        Cliente clienteExistente = Cliente.builder().nome("Outro Cliente").numeroConta("2007").saldo(100.0).build();
        when(clienteRepository.findByNumeroConta(clienteDTO.getNumeroConta())).thenReturn(Optional.of(clienteExistente));

        ClienteException exception = assertThrows(ClienteException.class, () -> {
            clienteService.cadastrarCliente(clienteDTO);
        });

        assertEquals("Número de conta já existente: 12345", exception.getMessage());
        verify(clienteRepository, times(1)).findByNumeroConta(clienteDTO.getNumeroConta());
        verify(clienteRepository, times(0)).save(any(Cliente.class));
    }
}
