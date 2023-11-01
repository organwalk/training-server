package com.training.common.entity.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterListReq {
    private List<Chapter> list;
    private static class Chapter{
        private int id;
        private String chapter_name;
        private int lesson_id;
    }
}
