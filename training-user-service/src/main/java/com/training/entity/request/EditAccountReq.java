package com.training.entity.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 定义编辑指定用户请求实体
 * by organwalk 2023-10-19
 */
@Data
@AllArgsConstructor
public class EditAccountReq {
    @NotBlank(message = "real_name不能为空")
    private String real_name;   // 真实姓名
    @Length(min = 6, message = "password长度不能小于6")
    @NotBlank(message = "password不能为空")
    private String password;    // 密码
    @Pattern(regexp = "^1\\d{10}$", message = "mobile为11位数的手机号字符串")
    private String mobile;  // 手机号
    @Min(value = 1, message = "auth字段必须是1、2或3的整数类型")
    @Max(value = 3, message = "auth字段必须是1、2或3的整数类型")
    private Integer auth_id; // 权限ID

}
