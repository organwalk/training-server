package com.training.gateway.config;

import com.training.gateway.interceptor.AuthenticationFilter;
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
                .route("training-user-service",r -> r.path("/api/user/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://training-user-service"))
                .route("training-department-service",r -> r.path("/api/dept/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://training-department-service"))
                .route("training-resource",r -> r.path("/api/resource/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://training-resource"))
                .route("training-plan-service",r -> r.path("/api/training/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://training-plan-service"))
                .route("training-learn-service",r -> r.path("/api/learn/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://training-learn-service"))
                .route("training-progress-service",r -> r.path("/api/progress/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://training-progress-service"))
                .route("training-push-service",r -> r.path("/api/push/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri("lb://training-push-service"))
                .build();
    }
}
