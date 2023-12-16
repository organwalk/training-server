package com.training.gateway.utils;

import com.alibaba.fastjson.JSON;
import com.training.gateway.entity.HeadersAuthInfo;
import com.training.gateway.entity.Respond;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 定义拦截后的提示信息响应
 * by organwalk 2023-10-19
 */
@Component
public class FilterUtil {

    /**
     * 获取权限失败的响应
     * @param exchange HTTP请求上下文
     * @param code 自定义状态码
     * @param msg 提示信息
     * @return 自定义响应体
     * by organwalk 2023-10-19
     */
    public Mono<Void> resAuthFail(ServerWebExchange exchange, Integer code, String msg){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String resBody = JSON.toJSONString(new Respond(code, msg));
        DataBuffer buffer = response.bufferFactory().wrap(resBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 获取请求头上的授权信息
     * @param exchange HTTP请求上下文
     * @return 一个包含授权信息的对象
     * by organwalk 2023-10-19
     */
    public HeadersAuthInfo getHeadersAuthInfo(ServerWebExchange exchange){
        HttpHeaders headers = exchange.getRequest().getHeaders();
        return new HeadersAuthInfo(
                headers.getFirst("username"),
                headers.getFirst("access_token"),
                headers.getFirst("auth")
        );
    }

    /**
     * 获取接口权限标记和请求权限对应Map
     * @return 映射Map
     * by organwalk 2023-10-19
     */
    public Map<String, String> authMap(){
        Map<String, String> authMap = new HashMap<>();
        authMap.put("v2", "teacher");
        authMap.put("v3", "admin");
        return authMap;
    }
}
