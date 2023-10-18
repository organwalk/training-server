package entity;

import lombok.Builder;
import lombok.Data;

/**
 * 通用信息响应
 * by organwalk 2023-10-18
 */
@Data
@Builder
public class MsgRespond {
    private Integer code;
    private String msg;

    public static MsgRespond success(String msg) {
        return MsgRespond.builder()
                .code(2002)
                .msg(msg)
                .build();
    }

    public static MsgRespond fail(String msg) {
        return MsgRespond.builder()
                .code(5005)
                .msg(msg)
                .build();
    }
}
