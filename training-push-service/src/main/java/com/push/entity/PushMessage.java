package com.push.entity;

import lombok.Data;

import java.util.List;

@Data
public class PushMessage {
    private String message_level;
    private String message;
    private List<String> to_uid_list;
}
