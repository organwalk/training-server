package com.training.learn.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CommentList {
    private int id;
    private int user_id;
    private String real_name;
    private String dept_name;
    private String content;
    private String creat_datetime;
    private int like_sum;
    private Integer like_state;
    private Reply_Obj replyObj;

    @Data
    @AllArgsConstructor
    public static class Reply_Obj{
        private int total;
        private List<ReplyList> list;
    }
}
