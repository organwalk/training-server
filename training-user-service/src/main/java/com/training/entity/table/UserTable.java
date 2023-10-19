package com.training.entity.table;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 定义t_user表结构对应实体
 * by organwalk 2023-10-18
 */
@Data
@TableName("t_user")
public class UserTable {
    private Integer id;
    private String username;
    private String password;
    private String realName;
    private String mobile;
    private Integer authId;
    private String extra;

    @TableField(exist = false)
    private AuthTable auth;

    public UserTable(Integer id, String username, String password, String realName, String mobile, Integer authId, String extra) {
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.mobile = mobile;
        this.authId = authId;
    }
}
