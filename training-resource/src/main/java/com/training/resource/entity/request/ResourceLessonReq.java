package com.training.resource.entity.request;

import jakarta.validation.constraints.Digits;
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
}
