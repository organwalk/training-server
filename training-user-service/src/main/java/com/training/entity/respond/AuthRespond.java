package com.training.entity.respond;

import lombok.Data;

/**
 * 用户登录获取授权接口响应
 * by organwalk 2023-10-18
 */
@Data
public class AuthRespond {
    private Integer uid;
    private String username;
    private String access_token;
    private String auth_name;

    public AuthRespond(Integer uid, String username, String access_token, String auth_name) {
        this.uid = uid;
        this.username = username;
        this.access_token = access_token;
        this.auth_name = auth_name;
    }
}
