package com.training.resource.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@TableName("t_resource_normal")
@AllArgsConstructor
public class ResourceNormalTable {
    private Integer id;
    private String resourceName;
    private String resourcePath;
    private Integer deptId;
    private Integer tagId;
    private Integer upId;
    private String upDatetime;
}
