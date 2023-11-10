package com.training.learn.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_learn_comment")
public class Comment {
    private Integer id;
    private Integer userId;
    private Integer lessonId;
    private Integer chapterId;
    private String content;
    private String createDatetime;

}
