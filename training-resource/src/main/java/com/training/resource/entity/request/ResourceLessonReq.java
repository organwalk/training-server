package com.training.resource.entity.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 定义上传普通资源的请求
 * by organwalk 2023-11-03
 */
@Data
@AllArgsConstructor
public class ResourceLessonReq {
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
    private Integer lesson_id;
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "teacher_id必须为纯数字字段")
    private Integer teacher_id;
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "chapter_id必须为纯数字字段")
    private Integer chapter_id;
    private MultipartFile resource_file;
    @NotBlank(message = "file_hash字段不能为空")
    private String file_hash;
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "file_size必须为纯数字字段")
    @NotNull(message = "file_size不能为空")
    private Integer file_size;
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "file_chunks_sum必须为纯数字字段")
    @NotNull(message = "file_chunks_sum不能为空")
    private Integer file_chunks_sum;
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "file_now_chunk必须为纯数字字段")
    @NotNull(message = "file_now_chunk不能为空")
    private Integer file_now_chunk;
    @NotBlank(message = "file_origin_name字段不能为空")
    private String file_origin_name;
}
