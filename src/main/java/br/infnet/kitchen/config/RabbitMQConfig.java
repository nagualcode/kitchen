package br.infnet.kitchen.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue filaPedidos() {
        return new Queue("fila-pedidos", false);
    }
}
