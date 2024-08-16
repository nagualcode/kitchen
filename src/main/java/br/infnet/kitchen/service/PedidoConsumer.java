package br.infnet.kitchen.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.infnet.kitchen.model.MensagemPedido;
import br.infnet.kitchen.model.Pedido;
import br.infnet.kitchen.repository.PedidoRepository;

@Service
public class PedidoConsumer {

    @Autowired
    private PedidoRepository pedidoRepository;

    @RabbitListener(queues = "fila-pedidos")
    public void receberMensagem(MensagemPedido mensagem) {
        Pedido pedido = pedidoRepository.findById(mensagem.getPedidoId()).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        pedido.setStatus("EM PROCESSAMENTO");
        pedidoRepository.save(pedido);
        // Processar a mensagem (como atualizar o status do pedido)
    }
}
