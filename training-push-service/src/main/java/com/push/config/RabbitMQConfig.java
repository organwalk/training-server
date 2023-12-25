package com.push.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String LIKE_PUSH_EXCHANGE = "likePushExchange";
    public static final String LIKE_PUSH_QUEUE = "likePushQueue";
    public static final String LIKE_PUSH_ROUTING_KEY = "likePushRoutingKey";

    @Bean
    public DirectExchange likePushExchange() {
        return new DirectExchange(LIKE_PUSH_EXCHANGE);
    }

    @Bean
    public Queue likePushQueue() {
        return new Queue(LIKE_PUSH_QUEUE);
    }

    @Bean
    public Binding likePushBinding(Queue likePushQueue, DirectExchange likePushExchange) {
        return BindingBuilder.bind(likePushQueue).to(likePushExchange).with(LIKE_PUSH_ROUTING_KEY);
    }
}
