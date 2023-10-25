package com.training.plan.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.MsgRespond;
import com.training.plan.client.UserClient;
import com.training.plan.mapper.TrainPlanStudentMapper;
import com.training.plan.service.TrainPlanStudentService;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Objects;
/**
 * 计划学生管理业务具体实现
 */
@Service
@AllArgsConstructor
@Transactional
public class TrainPlanStudentServiceImpl implements TrainPlanStudentService {
    private final TrainPlanStudentMapper trainPlanStudentMapper;
    private final UserClient userClient;
    /**
     * 创建计划的具体实现
     * @param student_id 学生id
     * @param plan_id 计划id
     * @return 根据处理结果返回对应消息
     */

    @Override
    public MsgRespond insertTrainPlanStudent(int student_id,int plan_id) {
        String CheckResult = judgeExit(student_id);
        if (!CheckResult.isBlank()){
            return MsgRespond.fail(CheckResult);
        }
        trainPlanStudentMapper.insertTrainPlanStudent(student_id,plan_id);
        return MsgRespond.success("添加成功！");
    }
    /**
     * 判断学生是否存在以及是否是学生的具体实现
     * @param training_student_id 学生id
     * @return 根据处理结果返回对应消息
     */
    private String judgeExit(int training_student_id){
        Integer Id = trainPlanStudentMapper.CheckStuInForm(training_student_id);
        if(Objects.nonNull(Id)){
            return "该用户已在培训计划里";
        }
        JSONObject resObject = userClient.getUserAccountByUid(training_student_id);
        if (Objects.equals(resObject.get("code"), 5005)){
            return "该员工不存在，无法添加!";
        }
        if(!Objects.equals(resObject.getJSONObject("data").get("authId"),1)){
            return "该用户不是员工，无法添加";
        }
        return "";

    }


}
