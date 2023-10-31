package com.training.plan.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private int id;
    private String real_name;
    private String mobile;
    private int auth_id;
    private String extra;
    private Auth auth;

    @Data

    public static class Auth{
        private int id;
        private String auth_name;

        public Auth(int authId, String authName) {
            this.id = authId;
            this.auth_name = authName;
        }
    }
}
