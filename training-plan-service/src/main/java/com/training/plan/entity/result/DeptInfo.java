package com.training.plan.entity.result;

import lombok.Data;

@Data
public class DeptInfo {
    private int id;
    private String dept_name;
    private int head_id;
    private String extra;

    public DeptInfo(int id, String dept_name, int head_id, String extra) {
        this.id = id;
        this.dept_name = dept_name;
        this.head_id = head_id;
        this.extra = extra;
    }
}
