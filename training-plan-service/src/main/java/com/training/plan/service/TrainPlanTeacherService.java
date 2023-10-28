package com.training.plan.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;


public interface TrainPlanTeacherService {
    //加入教师
    MsgRespond insertTrainPlanTeacher(int train_plan_id,int training_teacher_id);
    //获取指定计划id的所有教师
    DataRespond getAllTeaByPlanId(int plan_id,int page_size,int offset);

    //删除指定教师
    MsgRespond deleteTea(int t_id);


}
