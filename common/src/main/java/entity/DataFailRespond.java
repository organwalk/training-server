package entity;

import lombok.Builder;
import lombok.Data;

/**
 * 通用数据型失败响应
 * by organwalk 2023-10-18
 */
@Data
@Builder
public class DataFailRespond implements DataRespond {
    private Integer code;
    private String msg;

    public static DataFailRespond fail(String msg) {
        return DataFailRespond.builder()
                .code(5005)
                .msg(msg)
                .build();
    }
}
