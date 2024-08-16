package br.infnet.kitchen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import br.infnet.kitchen.model.MensagemPedido;
import br.infnet.kitchen.model.Pedido;
import br.infnet.kitchen.repository.PedidoRepository;

import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Pedido criarPedido(Pedido pedido) {
        pedido.setStatus("RECEBIDO");
        Pedido novoPedido = pedidoRepository.save(pedido);
        MensagemPedido mensagem = new MensagemPedido(novoPedido.getId(), novoPedido.getDescricao(), novoPedido.getStatus());
        rabbitTemplate.convertAndSend("fila-pedidos", mensagem);
        return novoPedido;
    }

    public Pedido obterPedidoPorId(Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        return pedido.orElse(null);
    }

    public Pedido atualizarPedido(Long id, Pedido pedidoAtualizado) {
        Pedido pedidoExistente = obterPedidoPorId(id);
        if (pedidoExistente != null) {
            pedidoExistente.setDescricao(pedidoAtualizado.getDescricao());
            pedidoExistente.setStatus(pedidoAtualizado.getStatus());
            return pedidoRepository.save(pedidoExistente);
        }
        return null;
    }
}
