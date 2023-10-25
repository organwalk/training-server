package com.training.plan.service;

import com.training.common.entity.MsgRespond;


public interface TrainPlanStudentService {
    //加入学生
    MsgRespond insertTrainPlanStudent(int student_id,int plan_id);


}
