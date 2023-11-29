package com.push.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_push_notification_source")
public class NotificationSource {
    private Integer id;
    private String sourceType;
}
