package com.tradingEngine.stockTrade.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "trading.exchange";

    // audit config
    public static final String AUDIT_QUEUE = "trading.audit.queue";
    public static final String ROUTING_KEY_AUDIT = "trading.audit.#";


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue auditQueue() {
        return new Queue(AUDIT_QUEUE,true);
    }

    @Bean
    public Binding auditBinding(Queue auditQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(auditQueue)
                .to(exchange)
                .with(ROUTING_KEY_AUDIT);
    }
}
