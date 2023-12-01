package com.push.entity;

import lombok.Data;

import java.util.List;

@Data
public class PushNotification {
    private String notification_type;
    private String notification_content;
    private String notification_source_type;
    private Integer notification_quote_id;
    private List<Integer> notification_receiver_list;
}