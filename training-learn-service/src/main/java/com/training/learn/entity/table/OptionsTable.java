package com.training.learn.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_learn_test_options")
public class OptionsTable {
    private int id;
    private String option;
    private int isMore;
    private int question_id;
}
