package com.push.entity;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PushNotification {
    private String access_token;
    private String notification_type;
    private String notification_content;
    private String notification_source_type;
    private Integer notification_quote_id;
    private List<Integer> notification_receiver_list;
}
