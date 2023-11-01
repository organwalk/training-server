package com.training.progress.servier;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;

public interface ProgressService {
    MsgRespond MarkChapterComplete(int lesson_id,int chapter_id,int student_id);

    DataRespond getAllPlanProgressList(int page_size,int offset);
}
