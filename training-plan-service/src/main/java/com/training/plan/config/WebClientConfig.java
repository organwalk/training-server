package com.training.plan.config;

import com.training.plan.client.DeptClient;
import com.training.plan.client.ProgressClient;
import com.training.plan.client.ResourceClient;
import com.training.plan.client.UserClient;
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
    public ProgressClient progressClient(){
        WebClient client = WebClient.builder().build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(ProgressClient.class);
    }

    @Bean
    public ResourceClient resourceClient(){
        WebClient client = WebClient.builder().build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(ResourceClient.class);
    }
}
