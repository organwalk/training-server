package com.training.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 定义请求头部的授权信息实体
 * by organwalk 2023-10-19
 */
@Data
@AllArgsConstructor
public class HeadersAuthInfo {
    private String username;
    private String accessToken;
    private String authName;
}
