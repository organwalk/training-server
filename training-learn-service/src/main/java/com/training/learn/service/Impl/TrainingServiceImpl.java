package com.training.learn.service.Impl;

import com.alibaba.fastjson.JSONObject;

import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataPagingSuccessRespond;
import com.training.common.entity.DataRespond;
import com.training.learn.client.PlanClient;
import com.training.learn.client.UserClient;
import com.training.learn.entity.respond.Plan;
import com.training.learn.entity.respond.TeacherInfo;
import com.training.learn.entity.result.LessonResult;
import com.training.learn.entity.result.PlanResult;
import com.training.learn.entity.result.ProgressLesson;
import com.training.learn.mapper.TrainingMapper;
import com.training.learn.service.TrainingService;
import com.training.learn.utils.ComputeUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class TrainingServiceImpl implements TrainingService {

    private final TrainingMapper trainingMapper;
    private final PlanClient planClient;
    private final UserClient userClient;




    /**
     *   获取指定学员所具有的培训计划列表
     * @param  student_id 学生id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getPlanListByStuId(int student_id,int page_size,int offset) {
        JSONObject req = userClient.getUserAccountByUid(student_id);
        if (Objects.equals(req.get("code"),5005)){
            return new DataFailRespond("该学生不存在！");
        }
        //获取指定学生的计划列表
        List<Integer> PlanIdList = trainingMapper.getPIdByStuId(student_id);
        List<PlanResult> list = new ArrayList<>();

        for(Integer i :PlanIdList){
            double x = getPresent(i,student_id);
            Plan p = getPlanById(i);
            PlanResult result = new PlanResult(i,p.getTraining_title(),p.getTraining_purpose(),p.getTraining_start_time(),p.getTraining_end_time(),p.getDept_id(),p.getTraining_state(),x,p.getExtra());
            list.add(result);
        }
        int endIndx = Math.min(offset + page_size,list.size());
        List<PlanResult> results = list.subList(offset,endIndx);
        return new DataPagingSuccessRespond("已成功获取该员工的培训计划列表",list.size(),results);
    }

    @Override
    public DataRespond getLessonByPIdAndStuId(int plan_id, int student_id, int page_size, int offset) {
        String StuMark = judgeStuExit(student_id);
        if(!StuMark.isBlank()){
            return new DataFailRespond(StuMark);
        }
        String PlanMark = judgePlanExit(plan_id);
        if (!PlanMark.isBlank()){
            return new DataFailRespond(PlanMark);
        }
        String Mark = judgeStuInPlan(student_id,plan_id);
        if (!Mark.isBlank()){
            return new DataFailRespond(Mark);
        }
        List<Integer> lessonIdList = trainingMapper.getLessonIdListByPId(plan_id);
        List<LessonResult> lessonResults = new ArrayList<>();
        for (Integer i:lessonIdList){
            JSONObject req = planClient.getLessonInfo(i);
            JSONObject data = req.getJSONObject("data");
            String lessonName = data.getString("lesson_name");
            String lessonDes = data.getString("lesson_des");
            String lessonState = data.getString("lesson_state");
            Integer teacherId = data.getInteger("teacher_id");
            TeacherInfo teacherInfo = getTeaInfo(teacherId);
            ProgressLesson lessonProgress = trainingMapper.getLessonProgressByLIdAndStuId(i,student_id);
            double x =0;
            if (lessonProgress!=null){
            x = ComputeUtil.getStuProgress(lessonProgress);
            }
            LessonResult lessonResult = new LessonResult(i,lessonName,lessonDes,lessonState,x,teacherId,teacherInfo);
            lessonResults.add(lessonResult);
        }
        int endIndx = Math.min(offset + page_size,lessonResults.size());
        List<LessonResult> results = lessonResults.subList(offset,endIndx);
        return new DataPagingSuccessRespond("已成功获取此培训计划下的课程列表",lessonResults.size(),results);
    }




    /**
     *  根据计划Id获取指定计划详细
     * @param  id 计划id
     * @return 根据处理结果返回对应消息
     */
    private Plan getPlanById(int id){
        JSONObject req = planClient.getPlanInfoById(id);
        JSONObject data = req.getJSONObject("data");
        JSONObject dept = data.getJSONObject("deptInfo");
        String training_title = data.getString("training_title");
        String training_purpose = data.getString("training_purpose");
        String training_start_time = data.getString("training_start_time") ;
        String training_end_time = data.getString("training_end_time");
        String training_state = data.getString("training_state");
        int dept_id = dept.getInteger("id");
        String extra = data.getString("extra");
        return new Plan(training_title,training_purpose,training_start_time,training_end_time,dept_id,training_state,extra);
    }




    /**
     *  获取指定学生指定计划的进度
     * @param  id 计划id
     * @return 根据处理结果返回对应消息
     */
    private double getPresent(int id,int student_id){
        List<Integer> lessonIdList = trainingMapper.getLessonIdByPId(id);
        double sum = 0;
        for (Integer i:lessonIdList){
            ProgressLesson progressLesson = trainingMapper.getProLessonByLIdAndStuId(i,student_id);
            double x = ComputeUtil.getStuProgress(progressLesson);
            sum+=x;
        }
        return sum;
    }



    /**
     *  判断学生是否在该计划内
     * @param  student_id 学生id
     * @param  plan_id 计划id
     * @return 根据处理结果返回对应消息
     */
    private String judgeStuInPlan(int student_id,int plan_id){
        Integer Mark = trainingMapper.judgeExitStuInPlan(plan_id,student_id);
        if (Objects.equals(Mark,0)){
            return "该学生不在该计划内！";
        }
        return "";
    }




    /**
     *  根据id获取教师信息
     * @param  id 学生id
     * @return 根据处理结果返回对应消息
     */
    private TeacherInfo getTeaInfo(int id){
        JSONObject req = userClient.getUserAccountByUid(id);
        JSONObject data = req.getJSONObject("data");
        String realName = data.getString("realName");
        String mobile = data.getString("mobile");
        return new TeacherInfo(realName,mobile);
    }



    /**
     *  判断学生是否存在
     * @param  student_id 学生id
     * @return 根据处理结果返回对应消息
     */
    private String judgeStuExit(int student_id){
        JSONObject req = userClient.getUserAccountByUid(student_id);
        if (Objects.equals(req.get("code"),5005)){
            return "该学生不存在！";
        }
        return "";
    }



    /**
     *  判断计划是否存在
     * @param  plan_id 计划id
     * @return 根据处理结果返回对应消息
     */
    private String judgePlanExit(int plan_id){
        JSONObject req = planClient.getPlanInfoById(plan_id);
        if (Objects.equals(req.get("code"),5005)){
            return "该计划不存在！";
        }
        return "";
    }








}
