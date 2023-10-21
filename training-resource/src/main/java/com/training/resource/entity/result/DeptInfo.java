package com.training.resource.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class DeptInfo {
    private Integer id;
    private String dept_name;
    private Integer head_id;
    private String extra;
}
