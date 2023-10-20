package com.training.department.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_dept_worker")
public class DeptWorkerTable {
    private Integer id;
    private Integer deptId;
    private Integer uid;
    private String extra;
}
