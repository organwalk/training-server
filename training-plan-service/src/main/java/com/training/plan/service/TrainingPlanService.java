package com.training.plan.service;


import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.plan.entity.request.PlanUpdateReq;
import com.training.plan.entity.request.TestReq;
import com.training.plan.entity.request.TrainingPlanReq;

import java.text.ParseException;

/**
 * by zhaozhifeng 2023-10-25
 */

public interface TrainingPlanService {
    //创建计划
    MsgRespond creatTrainingPlan(TrainingPlanReq req) throws ParseException;
    //获取计划列表
    DataRespond getAllPlan(int page_size, int offset);
    //获取指定部门的所有计划
    DataRespond getDeptAllPlan(int dept_id,int page_size,int offset);

    MsgRespond addTest(int lesson_id, TestReq req);
    //通过id获取指定计划
    DataRespond getTrainPlanById(int plan_id);
    //编辑指定计划
    MsgRespond UpdatePlan(int id, PlanUpdateReq req) throws ParseException;
    //修改计划状态
    MsgRespond changeState(String state,int id);

    // 模糊查询计划信息获取列表
    DataRespond getAllPlanByKeyword(String keyword, int page_size, int offset);
}
