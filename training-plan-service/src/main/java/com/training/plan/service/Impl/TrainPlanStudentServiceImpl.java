package com.training.plan.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.*;
import com.training.common.entity.req.UserInfoListReq;
import com.training.plan.client.UserClient;
import com.training.plan.entity.respond.TeacherInfo;
import com.training.plan.entity.result.User;
import com.training.plan.mapper.TrainPlanStudentMapper;
import com.training.plan.reposoty.PlanCache;
import com.training.plan.service.TrainPlanStudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final PlanCache planCache;
    /**
     * 添加学生进入计划的具体实现
     * @param student_id 学生id
     * @param plan_id 计划id
     * @return 根据处理结果返回对应消息
     */

    @Override
    public MsgRespond insertTrainPlanStudent(int student_id,int plan_id) {
        //判断计划名是否存在
        String CheckResult = judgeExit(student_id,plan_id);
        if (!CheckResult.isBlank()){
            return MsgRespond.fail(CheckResult);
        }
        //添加学生
        trainPlanStudentMapper.insertTrainPlanStudent(student_id,plan_id);
        return MsgRespond.success("添加成功！");
    }
    /**
     * 根据计划id获取所有学员信息的具体实现
     * @param plan_id 计划id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getAllStuByPlanId(int plan_id, int page_size, int offset) {
        //判断计划是否存在
        String CheckMark = judgePlanExit(plan_id);
        if (!CheckMark.isBlank()){
            return new DataFailRespond(CheckMark);
        }
        Integer sumMark = trainPlanStudentMapper.getPlanStuCount(plan_id);
        //判断缓存中是否拥有，有则从redis获取
        String key = plan_id+"-"+page_size+"-"+offset;
        if (planCache.getStuList(key)!=null){
            String result = (String) planCache.getStuList(key);
            List<TeacherInfo> info = JSON.parseArray(result, TeacherInfo.class);
            return  new DataPagingSuccessRespond("查询成功！",sumMark,info);
        }
        //获取计划中所有学生id
        List<Integer> AllStuId = trainPlanStudentMapper.getAllStuId(plan_id);
        //调用远程接口获取学生详细信息
        JSONArray StuList = userClient.getUserInfoByUidList(new UserInfoListReq(AllStuId));
        List<User> userList = JSONArray.parseArray(StuList.toJSONString(),User.class);
        //获取所有id
        List<Integer> IdList = trainPlanStudentMapper.getAllIdByPlanID(plan_id);
        List<TeacherInfo> AllStuList = new ArrayList<>();
        for(int i= 0;i<userList.size();i++){
            TeacherInfo teacherInfo = new TeacherInfo(IdList.get(i),AllStuId.get(i),userList.get(i));
            AllStuList.add(teacherInfo);
        }
        //将数据缓存
        planCache.saveStu(key,AllStuList);
        return new DataPagingSuccessRespond("查询成功！",sumMark,AllStuList);
    }
    /**
     * 根据id删除学生的具体实现
     * @param id 数据id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond deleteStu(int id) {
        //判断学生是否在该计划
        Integer ExitMark = trainPlanStudentMapper.ExitJudge(id);
        if(Objects.equals(ExitMark,0)){
            return MsgRespond.fail("该学生未在该计划内！");
        }
        //删除学生
        Integer i = trainPlanStudentMapper.DeleteStu(id);
        if (i<=0){
            return MsgRespond.fail("删除失败！");
        }
        //删除缓存
        Map<Object, Object> stuList = planCache.getStuAll();
        for(Object key:stuList.keySet()){
            String StrKey = key.toString();
            String[] parts = StrKey.split("-");
            String value = parts[0];
            if (value.equals(String.valueOf(id))){
                planCache.DeleteStu(key);
            }
        }
        return MsgRespond.success("已成功删除此学员!");
    }

    /**
     * 判断学生是否存在以及是否是学生的具体实现
     * @param training_student_id 学生id
     * @return 根据处理结果返回对应消息
     */
    private String judgeExit(int training_student_id,int plan_id){
        Integer Id = trainPlanStudentMapper.CheckStuInForm(training_student_id,plan_id);
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
    /**
     * 判断计划是否存在具体实现
     * @param plan_id 计划id
     * @return 根据处理结果返回对应消息
     */
    private String judgePlanExit(int plan_id){
       Integer ExitCheck = trainPlanStudentMapper.getPlanByPlanId(plan_id);
        if (Objects.equals(ExitCheck,0)){
            return "该计划不存在！";
        }
        return "";
    }






}
