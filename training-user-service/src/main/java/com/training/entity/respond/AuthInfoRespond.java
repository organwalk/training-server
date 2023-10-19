package com.training.entity.respond;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthInfoRespond {
    private String auth_name;
    private String access_token;
}
