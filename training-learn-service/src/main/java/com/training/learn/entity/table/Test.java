package com.training.learn.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_learn_test")
public class Test {
    private int id;
    private String test_title;
    private int lesson_id;
    private int teacher_id;
    private String start_datetime;
    private String end_datetime;
    private String creat_datetime;
    private int isRelease;
}
