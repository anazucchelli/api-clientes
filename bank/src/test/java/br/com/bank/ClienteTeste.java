package br.com.bank;
import br.com.bank.controller.ClienteController;
import br.com.bank.exception.ClienteException;
import br.com.bank.model.Cliente;
import br.com.bank.model.ClienteDTO;
import br.com.bank.repository.ClienteRepository;
import br.com.bank.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


@SpringBootTest
public class ClienteTeste {
	@InjectMocks
    private ClienteController clienteController;

    @Mock
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCadastrarCliente_Sucesso() throws ClienteException {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("John Doe");
        clienteDTO.setNumeroConta("123456");
        clienteDTO.setSaldo(100.0);

        Cliente cliente = new Cliente();
        cliente.setNome("John Doe");
        cliente.setNumeroConta("123456");
        cliente.setSaldo(100.0);

        when(clienteService.cadastrarCliente(clienteDTO)).thenReturn(cliente);

        ResponseEntity<Object> response = clienteController.cadastrarCliente(clienteDTO);

        assertEquals(CREATED.value(), response.getStatusCode().value());
        assertEquals("Cliente cadastrado com sucesso", response.getBody());
    }

    @Test
    void testCadastrarCliente_ClienteException() throws ClienteException {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("John Doe");
        clienteDTO.setNumeroConta("123456");
        clienteDTO.setSaldo(100.0);

        doThrow(new ClienteException("Número de conta já existente: " + clienteDTO.getNumeroConta()))
                .when(clienteService).cadastrarCliente(clienteDTO);

        ResponseEntity<Object> response = clienteController.cadastrarCliente(clienteDTO);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Erro ao cadastrar cliente: Número de conta já existente: 123456", response.getBody());
    }

    @Test
    void testListarClientes_Sucesso() throws ClienteException {
        Cliente cliente = new Cliente();
        when(clienteService.listarClientes()).thenReturn(Collections.singletonList(cliente));

        ResponseEntity<List<Cliente>> response = clienteController.listarClientes();

        assertEquals(OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testListarClientes_ClienteException() throws ClienteException {
        doThrow(new ClienteException("Lista de clientes está vazia")).when(clienteService).listarClientes();

        ClienteException thrown = assertThrows(ClienteException.class, () -> {
            clienteController.listarClientes();
        });

        assertEquals("Lista de clientes está vazia", thrown.getMessage());
    }

    @Test
    void testBuscarNumeroConta_Sucesso() throws ClienteException {
        Cliente cliente = new Cliente();
        when(clienteService.buscarNumeroConta("123456")).thenReturn(Optional.of(cliente));

        ResponseEntity<Cliente> response = clienteController.buscarNumeroConta("123456");

        assertEquals(OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(cliente, response.getBody());
    }

    @Test
    void testBuscarNumeroConta_ClienteException() throws ClienteException {
        when(clienteService.buscarNumeroConta("123456")).thenReturn(Optional.empty());

        ResponseEntity<Cliente> response = clienteController.buscarNumeroConta("123456");

        assertEquals(404, response.getStatusCode().value());
    }    
}
