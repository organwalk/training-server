package com.training.learn.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_learn_like")
public class Like {
    private int id;
    private int user_id;
    private int comment_id;
    private int reply_id;
    private int state;
    private String create_datetime;
}
