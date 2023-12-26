package com.training.learn.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String COMMENT_EXCHANGE_NAME = "commentFanoutExchange";
    public static final String COMMENT_USER_QUEUE = "commentUserQueue";
    public static final String COMMENT_PLAN_QUEUE = "commentPlanQueue";
    public static Integer COMMENT_CONSUMER_NUMBER = 2;

    @Bean
    public FanoutExchange commentFanoutExchange() {
        return new FanoutExchange(COMMENT_EXCHANGE_NAME);
    }

    @Bean
    public Queue commentUserQueue() {
        return new Queue(COMMENT_USER_QUEUE);
    }

    @Bean
    public Queue commentPlanQueue() {
        return new Queue(COMMENT_PLAN_QUEUE);
    }

    @Bean
    public Binding commentUserBinding(Queue commentUserQueue, FanoutExchange commentFanoutExchange) {
        return BindingBuilder.bind(commentUserQueue).to(commentFanoutExchange);
    }

    @Bean
    public Binding commentPlanBinding(Queue commentPlanQueue, FanoutExchange commentFanoutExchange) {
        return BindingBuilder.bind(commentPlanQueue).to(commentFanoutExchange);
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


    public static final String TEST_PROCESS_EXCHANGE = "testProcessExchange";
    public static final String TEST_PROCESS_QUEUE = "testProcessQueue";
    public static final String TEST_PROCESS_ROUTING_KEY = "testProcessRoutingKey";

    @Bean
    public DirectExchange testProcessExchange() {
        return new DirectExchange(TEST_PROCESS_EXCHANGE);
    }

    @Bean
    public Queue testProcessQueue() {
        return new Queue(TEST_PROCESS_QUEUE);
    }

    @Bean
    public Binding testProcessBinding(Queue testProcessQueue, DirectExchange testProcessExchange) {
        return BindingBuilder.bind(testProcessQueue).to(testProcessExchange).with(TEST_PROCESS_ROUTING_KEY);
    }


    public static final String LIKE_PROCESS_EXCHANGE = "likeProcessExchange";
    public static final String LIKE_PROCESS_QUEUE = "likeProcessQueue";
    public static final String LIKE_PROCESS_ROUTING_KEY = "likeProcessRoutingKey";

    @Bean
    public DirectExchange likeProcessExchange() {
        return new DirectExchange(LIKE_PROCESS_EXCHANGE);
    }

    @Bean
    public Queue likeProcessQueue() {
        return new Queue(LIKE_PROCESS_QUEUE);
    }

    @Bean
    public Binding likeProcessBinding(Queue likeProcessQueue, DirectExchange likeProcessExchange) {
        return BindingBuilder.bind(likeProcessQueue).to(likeProcessExchange).with(LIKE_PROCESS_ROUTING_KEY);
    }


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


    public static final String REPLY_LIKE_PROCESS_EXCHANGE = "replyLikeProcessExchange";
    public static final String REPLY_LIKE_PROCESS_QUEUE = "replyLikeProcessQueue";
    public static final String REPLY_LIKE_PROCESS_ROUTING_KEY = "replyLikeProcessRoutingKey";

    @Bean
    public DirectExchange replyLikeProcessExchange() {
        return new DirectExchange(REPLY_LIKE_PROCESS_EXCHANGE);
    }

    @Bean
    public Queue replyLikeProcessQueue() {
        return new Queue(REPLY_LIKE_PROCESS_QUEUE);
    }

    @Bean
    public Binding replyLikeProcessBinding(Queue replyLikeProcessQueue, DirectExchange replyLikeProcessExchange) {
        return BindingBuilder.bind(replyLikeProcessQueue).to(replyLikeProcessExchange).with(REPLY_LIKE_PROCESS_ROUTING_KEY);
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
