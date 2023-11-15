package com.training.plan.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;

import java.util.List;


public interface TrainPlanTeacherService {
    //加入教师
    MsgRespond insertTrainPlanTeacher(int train_plan_id, List<Integer> teacherIdList);
    //获取指定计划id的所有教师
    DataRespond getAllTeaByPlanId(int plan_id,int page_size,int offset);

    //删除指定教师
    MsgRespond deleteTea(int plan_id, int t_id);

    // 获取指定教师所处的培训计划列表
    DataRespond getPlanListByTeacher(Integer teacher);


}
