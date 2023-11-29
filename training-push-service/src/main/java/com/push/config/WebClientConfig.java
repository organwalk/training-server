package com.push.config;

import com.push.client.LearnClient;
import com.push.client.PlanClient;
import com.push.client.TestClient;
import com.push.client.UserClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * 注册WebClient接口服务
 */
@Configuration
public class WebClientConfig {
    @Bean
    public PlanClient planClient() {
        WebClient client = WebClient.builder().build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(PlanClient.class);
    }

    @Bean
    public UserClient userClient() {
        WebClient client = WebClient.builder().build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(UserClient.class);
    }

    @Bean
    public TestClient testClient() {
        WebClient client = WebClient.builder().build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(TestClient.class);
    }

    @Bean
    public LearnClient learnClient() {
        WebClient client = WebClient.builder().build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(LearnClient.class);
    }
}
