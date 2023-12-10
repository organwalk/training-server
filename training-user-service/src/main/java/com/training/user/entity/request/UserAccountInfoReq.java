package com.training.user.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 定义用户所能修改的账号信息请求实体
 * by organwalk 2023-10-18
 */
@Data
@AllArgsConstructor
public class UserAccountInfoReq {

    private String mobile;  // 手机号

    private String password;    // 密码
}
