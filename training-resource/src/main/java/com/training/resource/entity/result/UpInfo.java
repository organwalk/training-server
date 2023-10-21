package com.training.resource.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class UpInfo {
    private Integer id;
    private String realName;
    private String mobile;
    private Integer auth_id;
    private String extra;
    private Auth auth;

    @Data
    private static class Auth{
        private Integer id;
        private String authName;
    }
}
