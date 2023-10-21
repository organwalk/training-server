package com.training.resource.entity.respond;

import com.baomidou.mybatisplus.annotation.TableField;
import com.training.resource.entity.result.DeptInfo;
import com.training.resource.entity.result.UpInfo;
import com.training.resource.entity.table.ResourceTagTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceNormalDetailRespond {
    private String resource_name;
    private Integer dept_id;
    private Integer tag_id;
    private Integer up_id;
    private String up_datetime;
    @TableField(exist = false)
    private DeptInfo deptInfo;
    @TableField(exist = false)
    private ResourceTagTable tagInfo;
    @TableField(exist = false)
    private UpInfo upInfo;
}
