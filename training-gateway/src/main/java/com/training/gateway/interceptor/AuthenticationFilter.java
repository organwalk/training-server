package com.training.gateway.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.training.gateway.client.UserClient;
import com.training.gateway.entity.HeadersAuthInfo;
import com.training.gateway.utils.FilterUtil;
import lombok.AllArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;


/**
 * 定义认证过滤器
 * by organwalk 2023-10-19
 */
@Component
@AllArgsConstructor
public class AuthenticationFilter implements GatewayFilter {

    private final UserClient userClient;
    private final FilterUtil filterUtil;
    /**
     * 通过用户服务获取令牌与请求头令牌比较，进行放行或拦截判断
     * @param exchange 一个HTTP请求-响应交换的上下文对象，包含请求、响应的相关信息
     * @param chain 过滤链，将请求传递给下一个过滤链
     * @return
     *
     * by organwalk 2023.04.02
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 获取本次请求的URL路径
        String reqURL = exchange.getRequest().getURI().getPath();

        // 如果是登录请求，则直接放行
        if (Objects.equals(reqURL, "/api/user/v1/auth")){
            return chain.filter(exchange);
        }

        // 如果是教材获取请求，也放行
        if (reqURL.contains("/api/resource/v1/lesson/load")){
            return chain.filter(exchange);
        }

        // 获取本次请求的URL路径的权限标识
        int thirdSlashIndex = reqURL.indexOf('/', reqURL.indexOf('/') + 1); // 获取第三个斜杠的索引
        int fourthSlashIndex = reqURL.indexOf('/', thirdSlashIndex + 1); // 获取第四个斜杠的索引
        String reqAuthMark = reqURL.substring(thirdSlashIndex + 1, fourthSlashIndex);

        // 获取请求头上的授权信息
        HeadersAuthInfo headers = filterUtil.getHeadersAuthInfo(exchange);
        // 获取真实权限和通行令牌
        JSONObject authInfo =  userClient.getUserAuthInfo(headers.getUsername());
        String realAuthName = (String) authInfo.get("auth_name");
        String realAccessToken = (String) authInfo.get("access_token");

        // 检查请求权限和接口权限标识是否一致
        if (Objects.equals("v4", reqAuthMark) && Objects.equals("none", headers.getAuthName())){
            return filterUtil.resAuthFail(exchange, 4003, "当前权限无法访问此接口");
        }else {
            Map<String, String> authMap = filterUtil.authMap();
            if (authMap.containsKey(reqAuthMark)
                    && !Objects.equals(authMap.get(reqAuthMark), headers.getAuthName())
                    && !Objects.equals("v1", reqAuthMark)) {
                return filterUtil.resAuthFail(exchange, 4003, "当前权限无法访问此接口");
            }
        }


        // 检查请求权限和实际权限是否一致
        if (!Objects.equals(headers.getAuthName(), realAuthName)){
            return filterUtil.resAuthFail(exchange, 4003, "当前权限与实际权限不一致，请修改后重试");
        }

        // 检查通行令牌是否存在或正确
        if (realAccessToken.isBlank() || !Objects.equals(headers.getAccessToken(), realAccessToken)){
            return filterUtil.resAuthFail(exchange, 4001, "授权信息已过期或不存在，请重新登录获取授权");
        }

        return chain.filter(exchange);
    }



}
