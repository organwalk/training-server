package com.training.learn.servier;

import com.training.common.entity.MsgRespond;

public interface ProgressService {
    MsgRespond MarkChapterComplete(int lesson_id,int chapter_id,int student_id);
}
