package com.training.learn.config;

import com.training.learn.client.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {

    @Bean
    public PlanClient planClient(){
        WebClient client = WebClient.builder().build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(PlanClient.class);
    }


    @Bean
    public UserClient userClient(){
        WebClient client = WebClient.builder().build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(UserClient.class);
    }


    @Bean
    public DeptClient deptClient(){
        WebClient client = WebClient.builder().build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(DeptClient.class);
    }


    @Bean
    public ResourceClient resourceClient(){
        WebClient client = WebClient.builder().build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(ResourceClient.class);
    }

    @Bean
    public ProgressClient progressClient(){
        WebClient client = WebClient.builder().build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(ProgressClient.class);
    }
}
