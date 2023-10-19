package com.training.config;

import com.training.interceptor.AuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 路由网关配置
 * by organwalk 2023-10-19
 */
@Configuration
@EnableDiscoveryClient
@AllArgsConstructor
public class GatewayConfig {
    private final AuthenticationFilter authenticationFilter;

    /**
     * 对各服务注入权限认证过滤器
     * @param builder 路由定位构建器
     * @return
     *
     * by organwalk 2023-10-19
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("training-user-service",r -> r.path("/api/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://training-user-service"))
                .build();
    }
}
