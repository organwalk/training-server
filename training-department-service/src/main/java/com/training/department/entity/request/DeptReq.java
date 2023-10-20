package com.training.department.entity.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 定义创建部门请求的映射实体
 * by organwalk 2023-10-20
 */
@Data
@AllArgsConstructor
public class DeptReq {
    @NotBlank(message = "dept_name字段不能为空")
    private String dept_name;
    @Min(value = 1, message = "head_id必须为大于0的整数")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private Integer head_id;
}
