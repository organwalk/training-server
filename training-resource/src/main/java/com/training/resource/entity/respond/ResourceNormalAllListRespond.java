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
public class ResourceNormalAllListRespond {
    private Integer id;
    private String resource_name;
    private Integer up_id;
    private String up_datetime;
    private Integer dept_id;
    private Integer tag_id;
    @TableField(exist = false)
    private UpInfo upInfo;
    @TableField(exist = false)
    private ResourceTagTable tagInfo;
    @TableField(exist = false)
    private DeptInfo deptInfo;
}
