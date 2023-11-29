package com.push.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_push_notification")
public class NotificationTable {
    private Integer id;
    private String notificationType;
    private String notificationContent;
    private Integer notificationSourceId;
    private Integer notificationQuoteId;
    private Integer notificationUid;
    private String createDatetime;
}
