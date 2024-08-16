package br.infnet.kitchen.controller;

import br.infnet.kitchen.model.Pedido;
import br.infnet.kitchen.repository.PedidoRepository;
import br.infnet.kitchen.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class PedidoControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(PedidoControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PedidoRepository pedidoRepository;

    @MockBean
    private PedidoService pedidoService;

    @Container
    private static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:management-alpine")
            .withExposedPorts(5672, 15672);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    public void setup() {
        logger.info("Setting up test environment: clearing PedidoRepository and configuring RabbitMQ");
        pedidoRepository.deleteAll();
        logger.info("Test environment setup complete.");
    }

    @Test
    public void testCriarPedido() throws Exception {
        logger.info("Starting test: testCriarPedido");

        // Dados simulados
        Pedido pedido = new Pedido("Pizza Margherita");
        logger.debug("Mocking PedidoService.criarPedido to return the Pedido: {}", pedido);
        
        when(pedidoService.criarPedido(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            logger.debug("Simulating PedidoRepository.save with Pedido: {}", p);
            return pedidoRepository.save(p);
        });

        logger.info("Performing POST request to create a Pedido");
        mockMvc.perform(post("/pedidos")
                        .contentType("application/json")
                        .content("{\"descricao\": \"Pizza Margherita\"}"))
                .andExpect(status().isCreated());

        logger.info("Verifying if the Pedido was saved in the repository");
        List<Pedido> pedidos = pedidoRepository.findAll();
        if (pedidos.isEmpty()) {
            logger.error("No Pedido was saved in the database.");
            throw new AssertionError("Nenhum pedido foi salvo no banco de dados.");
        }

        Pedido savedPedido = pedidos.get(0);
        assertEquals("Pizza Margherita", savedPedido.getDescricao());
        logger.info("Pedido saved successfully: {}", savedPedido);

        logger.info("Verifying if the Pedido was sent to the RabbitMQ queue");
        // Adicionar atraso para garantir que a mensagem foi processada
        TimeUnit.SECONDS.sleep(5);

        Object mensagemRecebida = rabbitTemplate.receiveAndConvert("fila-pedidos");
        assertNotNull(mensagemRecebida, "A mensagem na fila RabbitMQ não deve ser nula.");
        logger.info("Message successfully received from RabbitMQ queue: {}", mensagemRecebida);

        logger.info("Test testCriarPedido completed successfully.");
    }
}
