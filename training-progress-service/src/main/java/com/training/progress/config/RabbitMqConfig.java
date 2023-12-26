package com.training.progress.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String MARK_CHAPTER_EXCHANGE = "markChapterExchange";
    public static final String MARK_CHAPTER_QUEUE = "markChapterQueue";
    public static final String MARK_CHAPTER_ROUTING_KEY = "markChapterRoutingKey";

    @Bean
    public DirectExchange markChapterExchange() {
        return new DirectExchange(MARK_CHAPTER_EXCHANGE);
    }

    @Bean
    public Queue markChapterQueue() {
        return new Queue(MARK_CHAPTER_QUEUE);
    }

    @Bean
    public Binding markChapterBinding(Queue markChapterQueue, DirectExchange markChapterExchange) {
        return BindingBuilder.bind(markChapterQueue).to(markChapterExchange).with(MARK_CHAPTER_ROUTING_KEY);
    }
}
