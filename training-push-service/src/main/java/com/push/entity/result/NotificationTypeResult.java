package com.push.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationTypeResult{
    private Integer id;
    private String notificationType;
    private String notificationContent;
    private Integer notificationSourceId;
    private Integer notificationQuoteId;
    private Integer notificationUid;
    private String createDatetime;
    private Integer isRead;
}
