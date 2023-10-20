package com.training.user.entity.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 定义创建账号请求实体
 * by organwalk 2023-10-18
 */
@Data
public class AccountReq {
    @NotBlank(message = "username不能为空")
    private String username;    // 用户名
    @Length(min = 6, message = "password长度不能小于6")
    @NotBlank(message = "password不能为空")
    private String password;    // 密码
    @NotBlank(message = "real_name不能为空")
    private String real_name;   // 真实姓名
    @Pattern(regexp = "^1\\d{10}$", message = "mobile为11位数的手机号字符串")
    private String mobile;  // 手机号
    @Min(value = 1, message = "auth字段必须是1、2或3的整数类型")
    @Max(value = 3, message = "auth字段必须是1、2或3的整数类型")
    private Integer auth_id; // 权限ID
}
