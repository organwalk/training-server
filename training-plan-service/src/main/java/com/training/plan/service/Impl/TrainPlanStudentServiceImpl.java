package com.training.plan.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.*;
import com.training.common.entity.req.UserInfoListReq;
import com.training.plan.client.DeptClient;
import com.training.plan.client.ProgressClient;
import com.training.plan.client.UserClient;
import com.training.plan.entity.respond.StudentInfo;

import com.training.plan.entity.result.User;
import com.training.plan.mapper.ChapterMapper;
import com.training.plan.mapper.LessonMapper;
import com.training.plan.mapper.TrainPlanStudentMapper;
import com.training.plan.mapper.TrainingPlanMapper;
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
 * by linguowei 2023-10-25
 */
@Service
@AllArgsConstructor
@Transactional
public class TrainPlanStudentServiceImpl implements TrainPlanStudentService {
    private final TrainPlanStudentMapper trainPlanStudentMapper;
    private final UserClient userClient;
    private final DeptClient deptClient;
    private final PlanCache planCache;
    private final ProgressClient progressClient;
    private final LessonMapper lessonMapper;
    private final ChapterMapper chapterMapper;
    private final TrainingPlanMapper PlanMapper;
    /**
     * 添加学生进入计划的具体实现
     * @param studentIdList 学生id列表
     * @param plan_id 计划id
     * @return 根据处理结果返回对应消息
     */

    @Override
    public MsgRespond insertTrainPlanStudent(List<Integer> studentIdList,int plan_id) {
        //判断计划名是否存在
        if (studentIdList.stream().anyMatch(item -> !judgeExit(item, plan_id).isBlank())) {
            return MsgRespond.fail("提供的学生列表中，部分学生已经在计划内");
        }
        List<Integer> nonNullStudentList = new ArrayList<>();
        for (Integer item : studentIdList) {
            JSONObject res = deptClient.getDeptId(item);
            if (Objects.equals(res.getInteger("code"), 5005)){
                return MsgRespond.fail(res.getString("msg"));
            }
            if (Objects.nonNull(res.getInteger("data"))){
                nonNullStudentList.add(item);
            }
        }
        //添加学生
        if (!nonNullStudentList.isEmpty()){
            trainPlanStudentMapper.insertTrainPlanStudent(nonNullStudentList,plan_id);
            clearCache(plan_id);
            List<Integer> lessonIdList = lessonMapper.getLIDByPId(plan_id);
            for(Integer i:lessonIdList){
                Integer sum = chapterMapper.getCountByLId(i);
                for (Integer j:studentIdList){
                    JSONObject res = progressClient.insertProgress(i,j,0,sum);
                    if (Objects.equals(res.getInteger("code"), 5005)){
                        return MsgRespond.fail(res.getString("res"));
                    }
                }
            }
        }
        if (studentIdList.size() == nonNullStudentList.size()){
            return MsgRespond.success("添加成功");
        }else if (nonNullStudentList.isEmpty()){
            return MsgRespond.fail("添加员工未被分配部门，无法被纳入培训计划");
        }else {
            return MsgRespond.success("添加成功，但部分员工未被分配部门，无法被纳入培训计划:" + nonNullStudentList);
        }
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
            List<StudentInfo> info = JSON.parseArray(result, StudentInfo.class);
            return  new DataPagingSuccessRespond("查询成功！",sumMark,info);
        }
        //获取计划中所有学生id
        List<Integer> AllStuId = trainPlanStudentMapper.getAllStuId(plan_id);
        //调用远程接口获取学生详细信息
        JSONObject res = userClient.getUserInfoByUidList(new UserInfoListReq(AllStuId));
        if (Objects.equals(res.getInteger("code"), 5005)){
            return new DataFailRespond(res.getString("msg"));
        }
        List<User> userList = JSON.parseArray(res.getJSONArray("data").toJSONString(), User.class);
        //获取所有id
        List<Integer> IdList = trainPlanStudentMapper.getAllIdByPlanID(plan_id);
        List<StudentInfo> AllStuList = new ArrayList<>();
        for(int i= 0;i<userList.size();i++){
            StudentInfo studentInfo = new StudentInfo(IdList.get(i),AllStuId.get(i),userList.get(i));
            AllStuList.add(studentInfo);
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
    public MsgRespond deleteStu(int planId, int id) {
        //判断学生是否在该计划
        Integer ExitMark = trainPlanStudentMapper.ExitJudge(planId);
        if(Objects.equals(ExitMark,0)){
            return MsgRespond.fail("该学生未在该计划内");
        }
        //删除学生
        Integer i = trainPlanStudentMapper.DeleteStu(id, planId);
        if (i<=0){
            return MsgRespond.fail("移除失败");
        }
        //删除缓存
        clearCache(planId);
        return MsgRespond.success("已成功移除此学员");
    }

    @Override
    public DataRespond getPlanListByStudent(Integer studentId) {
        List<Integer> planIdList = trainPlanStudentMapper.selectPlanIdListByStudentId(studentId);
        if (planIdList.isEmpty()) return new DataFailRespond("当前学员暂未被分配执行培训计划");
        // 批量查询
        return new DataSuccessRespond("已成功获取培训计划列表", PlanMapper.selectPlanListByIdList(planIdList));
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
            return resObject.getString("msg");
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
            return "学员列表为空，请进行添加";
        }
        return "";
    }

    private void clearCache(int planId){
        Map<Object, Object> stuList = planCache.getStuAll();
        for(Object key:stuList.keySet()){
            String StrKey = key.toString();
            String[] parts = StrKey.split("-");
            String value = parts[0];
            if (value.equals(String.valueOf(planId))){
                planCache.DeleteStu(key);
            }
        }
    }



}
