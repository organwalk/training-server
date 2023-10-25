package com.training.plan.service;

import com.training.common.entity.MsgRespond;


public interface TrainPlanTeacherService {
    //加入教师
    MsgRespond insertTrainPlanTeacher(int train_plan_id,int training_teacher_id);
}
