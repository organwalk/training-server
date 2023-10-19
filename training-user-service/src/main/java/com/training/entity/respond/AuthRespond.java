package com.training.entity.respond;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户登录获取授权接口响应
 * by organwalk 2023-10-18
 */
@Data
@AllArgsConstructor
public class AuthRespond {
    private Integer uid;
    private String username;
    private String access_token;
    private String auth_name;
}
