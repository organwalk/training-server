package entity;

import lombok.Data;

/**
 * 通用数据型失败响应
 * by organwalk 2023-10-18
 */
@Data
public class DataFailRespond implements DataRespond {
    private Integer code;
    private String msg;

    public DataFailRespond(String msg) {
        this.code = 5005;
        this.msg = msg;
    }
}
