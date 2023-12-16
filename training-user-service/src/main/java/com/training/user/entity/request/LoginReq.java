package com.training.user.entity.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 用户登录获取认证请求实体
 * by organwalk 2023-10-18
 */
@Data
public class LoginReq {
    @NotBlank(message = "username不能为空")
    private String username;
    @Length(min = 6, message = "password长度不能小于6")
    private String password;
}
