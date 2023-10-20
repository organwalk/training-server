package com.training.department.entity.request;

import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MembersReq {
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
    private Integer dept_id;
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "uid必须为纯数字字段")
    private Integer uid;
}
