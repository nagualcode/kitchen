package br.infnet.kitchen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.infnet.kitchen.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
