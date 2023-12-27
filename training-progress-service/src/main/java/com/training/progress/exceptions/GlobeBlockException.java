package com.training.progress.exceptions;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;

public class GlobeBlockException {
    public static DataRespond blockedGetLessonByPIdAndStuId(int plan_id, int student_id, int page_size, int offset, BlockException e){
        return new DataFailRespond("进度跟踪服务异常，无法获取学员课程进度百分比列表");
    }
    public static DataRespond blockedGetStudentIdList(Integer lessonId, BlockException e){
        return new DataFailRespond("进度跟踪服务异常，无法获取课程下的学员列表");
    }
    public static MsgRespond blockedUpdateStuLessonProgress(int lesson_id, int student_id, int over_chapter_sum, int lesson_chapter_sum, BlockException e){
        return MsgRespond.fail("进度跟踪服务异常，无法设置学员总体课程进度");
    }
    public static MsgRespond blockedLessonTrack(int plan_id, int teacher_id, Integer lesson_id, BlockException e){
        return MsgRespond.fail("进度跟踪服务异常，无法建立课程进度跟踪机制");
    }
    public static DataRespond blockedGetLessonIdListByPlanId(int plan_id, int page_size, int offset, BlockException e){
        return new DataFailRespond("进度跟踪服务异常，无法获取建立跟踪机制的课程列表");
    }
    public static MsgRespond blockedUpdateChapterSum(Integer sum, int lesson_id){
        return MsgRespond.fail("进度跟踪服务异常，无法更新章节总数");
    }
}
