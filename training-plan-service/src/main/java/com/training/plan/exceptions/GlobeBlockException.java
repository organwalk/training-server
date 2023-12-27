package com.training.plan.exceptions;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataRespond;

public class GlobeBlockException {
    public static DataRespond blockedGetTrainPlanById(int plan_id, BlockException e){
        return new DataFailRespond("培训管理服务异常，无法正常获取培训计划信息");
    }
    public static DataRespond blockedGetLessInfoById(Integer lesson_id, BlockException e){
        return new DataFailRespond("培训管理服务异常，无法正常获取课程信息");
    }
    public static DataRespond blockedGetChapterByLID(Integer lesson_id, BlockException e){
        return new DataFailRespond("培训管理服务异常，无法正常获取指定课程的所有章节");
    }
    public static DataRespond blockedGetChapterDetail(Integer chapterId, BlockException e){
        return new DataFailRespond("培训管理服务异常，无法正常获取章节名称");
    }
    public static DataRespond blockedDeleteByRLId(Integer resource_lesson_id, BlockException e){
        return new DataFailRespond("培训管理服务异常，无法正常删除视频测试题");
    }
    public static DataRespond blockedGetTeaAllLess(Integer teacher_id, int page_size, int offset, BlockException e){
        return new DataFailRespond("培训管理服务异常，无法正常获取教师课程");
    }
}
