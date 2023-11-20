package com.training.learn.entity.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class NoteReq {

    @NotBlank(message = "note_title不能为空")
    @Pattern(regexp = "^.{0,20}$", message = "note_title不能超过20个字符")
    private String note_title;

    @NotBlank(message = "note_des不能为空")
    @Pattern(regexp = "^.{0,50}$", message = "note_des不能超过50个字符")
    private String note_des;

    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "resource_note_id必须为纯数字字段")
    @NotNull(message = "resource_note_id不能为空")
    private int resource_note_id;
}
