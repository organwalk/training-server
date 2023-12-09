package com.training.learn.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_learn_reply")
public class Reply {
    private Integer id;
    private Integer user_id;
    private Integer comment_id;
    private String content;
    private String create_datetime;
}
