package com.training.department.entity.result;

import lombok.Data;

/**
 * 定义成员信息结果集
 * by organwalk 2023-10-20
 */
@Data
public class MembersInfo {
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
