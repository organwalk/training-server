package entity;

import lombok.Builder;
import lombok.Data;

/**
 * 通用数据型成功响应
 * by organwalk 2023-10-18
 */
@Data
@Builder
public class DataSuccessRespond implements DataRespond {
    private Integer code;
    private String msg;
    private Object data;

    public static DataSuccessRespond success(String msg, Object data) {
        return DataSuccessRespond.builder()
                .code(2002)
                .msg(msg)
                .data(data)
                .build();
    }
}
