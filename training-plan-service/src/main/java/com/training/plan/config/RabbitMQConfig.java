package com.training.plan.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String COMMENT_EXCHANGE_NAME = "commentFanoutExchange";
    public static final String COMMENT_PLAN_QUEUE = "commentPlanQueue";

    @Bean
    public FanoutExchange commentFanoutExchange() {
        return new FanoutExchange(COMMENT_EXCHANGE_NAME);
    }

    @Bean
    public Queue commentQueue() {
        return new Queue(COMMENT_PLAN_QUEUE);
    }

    @Bean
    public Binding commentBinding(Queue commentQueue, FanoutExchange commentFanoutExchange) {
        return BindingBuilder.bind(commentQueue).to(commentFanoutExchange);
    }

    public static final String COMMENT_RESULT_EXCHANGE_NAME = "commentResultExchange";
    public static final String COMMENT_RESULT_QUEUE_NAME = "commentResultQueue";
    public static final String COMMENT_RESULT_ROUTING_KEY = "commentResultRoutingKey";

    @Bean
    public DirectExchange commentResultExchange() {
        return new DirectExchange(COMMENT_RESULT_EXCHANGE_NAME);
    }

    @Bean
    public Queue commentResultQueue() {
        return new Queue(COMMENT_RESULT_QUEUE_NAME);
    }

    @Bean
    public Binding commentResultBinding(Queue commentResultQueue, DirectExchange commentResultExchange) {
        return BindingBuilder.bind(commentResultQueue).to(commentResultExchange).with(COMMENT_RESULT_ROUTING_KEY);
    }
}
