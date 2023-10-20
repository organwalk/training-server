package com.training.gateway.config;

import com.training.gateway.client.UserClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * 注册WebClient接口服务
 * by organwalk 2023-10-19
 */
@Configuration
public class WebClientConfig {
    /**
     * 创建一个使用HTTP请求的代理客户端
     * @return 使用HTTP请求的UserClient
     *
     * by organwalk 2023-10-19
     */
    @Bean
    public UserClient userClient(){
        WebClient client = WebClient.builder().build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(UserClient.class);
    }

}
