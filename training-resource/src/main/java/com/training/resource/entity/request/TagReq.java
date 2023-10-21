package com.training.resource.entity.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 创建编辑资源分类标签的请求实体
 * by organwalk 2023-10-21
 */
@Data
@AllArgsConstructor
public class TagReq {
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
    private Integer dept_id;
    @NotBlank(message = "tag_name字段不能为空")
    private String tag_name;
}
