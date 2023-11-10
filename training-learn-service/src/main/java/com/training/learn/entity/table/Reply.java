package com.training.learn.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_learn_reply")
public class Reply {
    private int id;
    private int user_id;
    private int comment_id;
    private String content;
    private String create_datetime;
}
