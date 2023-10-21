package com.training.resource.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_resource_tag")
public class ResourceTagTable {
    private Integer id;
    private String tag_name;
    private Integer dept_id;
}
