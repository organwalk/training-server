package com.training.user.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 定义t_auth表结构对应实体
 * by organwalk 2023-10-19
 */
@Data
@TableName("t_auth")
public class AuthTable {
    private Integer id;
    private String authName;
}
