package com.training.user.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 定义用户所能修改的账号信息请求实体
 * by organwalk 2023-10-18
 */
@Data
@AllArgsConstructor
public class UserAccountInfoReq {

    @Pattern(regexp = "^1\\d{10}$", message = "mobile为11位数的手机号字符串")
    private String mobile;  // 手机号

    @Length(min = 6, message = "password长度不能小于6")
    @NotBlank(message = "password不能为空")
    private String password;    // 密码
}
