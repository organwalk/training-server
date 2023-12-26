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

    public static final String REPLY_LIKE_PUSH_EXCHANGE = "replyLikePushExchange";
    public static final String REPLY_LIKE_PUSH_QUEUE = "replyLikePushQueue";
    public static final String REPLY_LIKE_PUSH_ROUTING_KEY = "replyLikePushRoutingKey";

    @Bean
    public DirectExchange replyLikePushExchange() {
        return new DirectExchange(REPLY_LIKE_PUSH_EXCHANGE);
    }

    @Bean
    public Queue replyLikePushQueue() {
        return new Queue(REPLY_LIKE_PUSH_QUEUE);
    }

    @Bean
    public Binding replyLikePushBinding(Queue replyLikePushQueue, DirectExchange replyLikePushExchange) {
        return BindingBuilder.bind(replyLikePushQueue).to(replyLikePushExchange).with(REPLY_LIKE_PUSH_ROUTING_KEY);
    }


    public static final String RELEASE_TEST_PUSH_EXCHANGE = "releaseTestPushExchange";
    public static final String RELEASE_TEST_PUSH_QUEUE = "releaseTestPushQueue";
    public static final String RELEASE_TEST_PUSH_ROUTING_KEY = "releaseTestPushRoutingKey";

    @Bean
    public DirectExchange releaseTestPushExchange() {
        return new DirectExchange(RELEASE_TEST_PUSH_EXCHANGE);
    }

    @Bean
    public Queue releaseTestPushQueue() {
        return new Queue(RELEASE_TEST_PUSH_QUEUE);
    }

    @Bean
    public Binding releaseTestPushBinding(Queue releaseTestPushQueue, DirectExchange releaseTestPushExchange) {
        return BindingBuilder.bind(releaseTestPushQueue).to(releaseTestPushExchange).with(RELEASE_TEST_PUSH_ROUTING_KEY);
    }

    public static final String REPLY_PUSH_EXCHANGE = "replyPushExchange";
    public static final String REPLY_PUSH_QUEUE = "replyPushQueue";
    public static final String REPLY_PUSH_ROUTING_KEY = "replyPushRoutingKey";

    @Bean
    public DirectExchange replyPushExchange() {
        return new DirectExchange(REPLY_PUSH_EXCHANGE);
    }

    @Bean
    public Queue replyPushQueue() {
        return new Queue(REPLY_PUSH_QUEUE);
    }

    @Bean
    public Binding replyPushBinding(Queue replyPushQueue, DirectExchange replyPushExchange) {
        return BindingBuilder.bind(replyPushQueue).to(replyPushExchange).with(REPLY_PUSH_ROUTING_KEY);
    }
}
