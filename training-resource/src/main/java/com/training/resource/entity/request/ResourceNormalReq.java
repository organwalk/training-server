package com.training.resource.entity.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 定义上传普通资源的请求
 * by organwalk 2023-10-21
 */
@Data
@AllArgsConstructor
public class ResourceNormalReq {
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
    private Integer dept_id;
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "tag_id必须为纯数字字段")
    private Integer tag_id;
    @NotBlank(message = "resource_name字段不能为空")
    private String resource_name;
    @NotNull(message = "需要上传一个文件")
    private MultipartFile resource_file;
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "up_id必须为纯数字字段")
    private Integer up_id;
}
