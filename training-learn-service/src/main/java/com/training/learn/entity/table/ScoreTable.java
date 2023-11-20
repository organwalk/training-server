package com.training.learn.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_learn_test_score")
public class ScoreTable {
    private Integer id;
    private Integer composite_score;
    private Integer must_composite_score;
    private Integer importance_type_composite_score;
    private Integer normal_type_composite_score;
    private Integer student_id;
    private Integer test_id;
}
