package com.training.department.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 定义t_dept表映射实体
 * by organwalk 2023-10-20
 */
@Data
@AllArgsConstructor
@TableName("t_dept")
public class DeptTable {
    private Integer id;
    private String deptName;
    private Integer headId;
    private String extra;
}
