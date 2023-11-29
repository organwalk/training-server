package com.push.entity.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDetail {
    private String notification_type;
    private String notification_content;
    private String notification_source_type;
    private Object notification_quote;
    private String notification_sender;
    private String create_datetime;
    private Integer is_read;
}
