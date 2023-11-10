package com.training.learn.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReplyList {
    private int id;
    private int user_id;
    private String real_name;
    private String dept_name;
    private String content;
    private String create_datetime;
    private int like_sum;
    private Integer like_state;

}
