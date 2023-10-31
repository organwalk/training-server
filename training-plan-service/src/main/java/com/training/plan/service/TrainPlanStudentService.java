package com.training.plan.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;


public interface TrainPlanStudentService {
    //加入学生
    MsgRespond insertTrainPlanStudent(int student_id,int plan_id);
    //根据计划id获取所有学生
    DataRespond getAllStuByPlanId(int plan_id,int page_size,int offset);
    //删除指定id的信息
    MsgRespond deleteStu(int id);


}