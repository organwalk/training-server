package com.training.entity.result;

import com.baomidou.mybatisplus.annotation.TableField;
import com.training.entity.table.AuthTable;
import lombok.Data;

@Data
public class UserInfo {
    private Integer id;
    private String realName;
    private String mobile;
    private Integer auth_id;
    private String extra;

    @TableField(exist = false)
    private AuthTable auth;

    public UserInfo(Integer id, String realName, String mobile, Integer auth_id, String extra) {
        this.id = id;
        this.realName = realName;
        this.mobile = mobile;
        this.auth_id = auth_id;
        this.extra = extra;
    }
}
