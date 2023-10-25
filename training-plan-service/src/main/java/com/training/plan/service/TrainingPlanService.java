package com.training.plan.service;


import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.plan.entity.request.TestReq;
import com.training.plan.entity.request.TrainingPlanReq;

import java.text.ParseException;


public interface TrainingPlanService {
    //创建计划
    MsgRespond creatTrainingPlan(TrainingPlanReq req) throws ParseException;

    DataRespond getAllPlan(int page_size, int offset);

    DataRespond getDeptAllPlan(int dept_id,int page_size,int offset);

    MsgRespond addTest(int lesson_id, TestReq req);
}
