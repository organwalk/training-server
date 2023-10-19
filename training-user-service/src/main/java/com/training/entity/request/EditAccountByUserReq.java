package com.training.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class EditAccountByUserReq {

    @Pattern(regexp = "^1\\d{10}$", message = "mobile为11位数的手机号字符串")
    private String mobile;  // 手机号

    @Length(min = 6, message = "password长度不能小于6")
    @NotBlank(message = "password不能为空")
    private String password;    // 密码
}
