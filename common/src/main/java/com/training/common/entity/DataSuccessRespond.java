package com.training.common.entity;

import lombok.Data;

/**
 * 通用数据型成功响应
 * by organwalk 2023-10-18
 */
@Data
public class DataSuccessRespond implements DataRespond {
    private Integer code;
    private String msg;
    private Object data;

    public DataSuccessRespond(String msg, Object data) {
        this.code = 2002;
        this.msg = msg;
        this.data = data;
    }
}
