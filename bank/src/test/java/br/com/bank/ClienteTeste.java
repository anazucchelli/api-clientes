package br.com.bank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.bank.model.Cliente;
import br.com.bank.repository.ClienteRepository;
import br.com.bank.service.ClienteService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ClienteTeste {

	@Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepository;

//    @Test
//    @Transactional
//    public void testCadastrarCliente() {
//        Cliente cliente = new Cliente();
//        cliente.setNome("Teste");
//        cliente.setNumeroConta("789456");
//        cliente.setSaldo(200.0);
//
//        Cliente salvo = clienteService.cadastrarCliente(cliente);
//
//        assertNotNull(salvo.getId()); // Verifica se o ID foi gerado
//        assertEquals("Teste", salvo.getNome());
//        assertEquals("789456", salvo.getNumeroConta());
//        assertEquals(200.0, salvo.getSaldo(), 0.001); // Adiciona uma tolerância para comparação de doubles
//    }
}
