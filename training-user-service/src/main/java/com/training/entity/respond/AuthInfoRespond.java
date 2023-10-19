package com.training.entity.respond;

import lombok.Data;

@Data
public class AuthInfoRespond {
    private String auth_name;
    private String access_token;

    public AuthInfoRespond(String auth_name, String access_token) {
        this.auth_name = auth_name;
        this.access_token = access_token;
    }
}
