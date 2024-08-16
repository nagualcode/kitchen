package br.infnet.kitchen.model;

import java.io.Serializable;

public class MensagemPedido implements Serializable {

    private static final long serialVersionUID = 1L; // Identificador único para a versão da classe

    private Long pedidoId;
    private String descricao;
    private String status;

    // Construtores
    public MensagemPedido() {}

    public MensagemPedido(Long pedidoId, String descricao, String status) {
        this.pedidoId = pedidoId;
        this.descricao = descricao;
        this.status = status;
    }

    // Getters e Setters
    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
