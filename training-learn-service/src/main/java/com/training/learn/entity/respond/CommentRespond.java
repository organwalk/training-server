package com.training.learn.entity.respond;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentRespond {
    private int user_id;
    private int lesson_id;
    private String content;
    private int chapter_id;
    private String create_datetime;
}
