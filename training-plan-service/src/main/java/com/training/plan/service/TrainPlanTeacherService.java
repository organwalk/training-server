package com.training.plan.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;


public interface TrainPlanTeacherService {
    //加入教师
    MsgRespond insertTrainPlanTeacher(int train_plan_id,int training_teacher_id);

    DataRespond getAllTeaByPlanId(int plan_id,int page_size,int offset);


}
