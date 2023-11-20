package com.training.learn.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_learn_test_type")
public class TypeTable {
    private Integer id;
    private String importance_type;
    private double weight;
    private Integer score;
}
