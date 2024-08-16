package br.infnet.kitchen.repository;

import br.infnet.kitchen.model.Pedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@DataJpaTest
public class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    public void testSaveAndFindPedido() {
        Pedido pedido = new Pedido();
        pedido.setDescricao("Pizza Margherita");

        Pedido savedPedido = pedidoRepository.save(pedido);
        Pedido foundPedido = pedidoRepository.findById(savedPedido.getId()).orElse(null);

        assertThat(foundPedido).isNotNull();
        assertThat(foundPedido.getDescricao()).isEqualTo("Pizza Margherita");
    }
}
