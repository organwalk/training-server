package com.training.resource.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@TableName("t_resource_note")
@AllArgsConstructor
public class ResourceNoteTable {
    private Integer id;
    private Integer lessonId;
    private Integer chapterId;
    private Integer upId;
    private String noteTitle;
    private String noteDes;
    private String notePath;
    private String upDatetime;
}
