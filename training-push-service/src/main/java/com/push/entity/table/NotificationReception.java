package com.push.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_push_notification_reception")
public class NotificationReception {
    private Integer id;
    private Integer notificationId;
    private Integer notificationReceiverId;
    private Integer isRead;
}
