package com.training.progress.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChapterList {
    private int over_chapter;
    private List<Integer> over_chapter_list;
}
