package com.training.resource.entity.respond;

import com.baomidou.mybatisplus.annotation.TableField;
import com.training.resource.entity.result.UpInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceNormalRespond {
    private Integer id;
    private String resource_name;
    private Integer up_id;
    private String up_datetime;
    @TableField(exist = false)
    private UpInfo upInfo;
}
