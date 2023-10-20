package com.training.common.entity;

import lombok.Data;

@Data
public class DataPagingSuccessRespond implements DataRespond{
    private Integer code;
    private String msg;
    private Integer total;
    private Object data;

    public DataPagingSuccessRespond(String msg, Integer total, Object data) {
        this.code = 2002;
        this.msg = msg;
        this.total = total;
        this.data = data;
    }
}
