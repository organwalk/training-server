package com.training.plan.service;


import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.plan.entity.request.TrainingPlanReq;

import java.text.ParseException;


public interface TrainingPlanService {
    //创建计划
    MsgRespond creatTrainingPlan(TrainingPlanReq req) throws ParseException;
    //获取部门列表
    DataRespond getAllPlan(int page_size, int offset);
    //获取指定部门列表
    DataRespond getDeptAllPlan(int dept_id,int page_size,int offset);
    //通过计划id获取计划
    DataRespond getTrainPlanById(int id);
}
