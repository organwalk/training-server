package com.training.resource.entity.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResourceNoteReq {
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
    @NotNull(message = "lesson_id不能为空")
    private Integer lesson_id;
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "chapter_id必须为纯数字字段")
    @NotNull(message = "chapter_id不能为空")
    private Integer chapter_id;
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "up_id必须为纯数字字段")
    @NotNull(message = "up_id不能为空")
    private Integer up_id;
    @NotBlank(message = "note_title不能为空")
    @Pattern(regexp = "^.{0,20}$", message = "note_title不能超过20个字符")
    private String note_title;
    @NotBlank(message = "note_des不能为空")
    @Pattern(regexp = "^.{0,50}$", message = "note_des不能超过50个字符")
    private String note_des;
    private MultipartFile note_file;
}
