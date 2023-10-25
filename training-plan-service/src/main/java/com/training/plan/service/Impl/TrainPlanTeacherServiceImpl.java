package com.training.plan.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.MsgRespond;
import com.training.plan.client.UserClient;
import com.training.plan.mapper.TrainPlanTeacherMapper;
import com.training.plan.service.TrainPlanTeacherService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Objects;
/**
 * 计划教师管理业务具体实现
 */
@Service
@AllArgsConstructor
@Transactional
public class TrainPlanTeacherServiceImpl implements TrainPlanTeacherService {
    private final TrainPlanTeacherMapper trainPlanTeacherMapper;
    private final UserClient userClient;
    /**
     * 创建计划的具体实现
     * @param training_teacher_id 教师id
     * @param train_plan_id 计划id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond insertTrainPlanTeacher(int train_plan_id,int training_teacher_id) {
        String CheckResult = judgeExit(training_teacher_id);
        if (!CheckResult.isBlank()){
            return MsgRespond.fail(CheckResult);
        }
        trainPlanTeacherMapper.insertTrainPlanTeacher(train_plan_id,training_teacher_id);
        return MsgRespond.success("添加教师成功！");
    }
    /**
     * 判断教师是否存在以及是否是学生的具体实现
     * @param training_teacher_id 教师id
     * @return 根据处理结果返回对应消息
     */
    private String  judgeExit(int training_teacher_id){
        Integer IdMark = trainPlanTeacherMapper.CheckTeaInForm(training_teacher_id);
        if(IdMark != 0){
            return "该教师已经存在！";
        }
        JSONObject resObject = userClient.getUserAccountByUid(training_teacher_id);
        if(Objects.equals(resObject.get("code"),5005)){
            return "该教师不存在，无法添加！";
        }
        if (!Objects.equals(resObject.getJSONObject("data").get("authId"),2)){
            return "该用户不是教师，无法添加！";
        }
        return "";
    }
}
