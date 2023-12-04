package com.training.learn.service.Impl;

import com.alibaba.fastjson.JSONObject;

import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataPagingSuccessRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.DataSuccessRespond;
import com.training.learn.client.PlanClient;
import com.training.learn.client.ProgressClient;
import com.training.learn.client.UserClient;
import com.training.learn.entity.respond.Chapter;
import com.training.learn.entity.respond.Plan;
import com.training.learn.entity.respond.TeacherInfo;
import com.training.learn.entity.result.*;
import com.training.learn.mapper.TrainingMapper;
import com.training.learn.service.TrainingService;
import com.training.learn.utils.ComputeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class TrainingServiceImpl implements TrainingService {

    private final TrainingMapper trainingMapper;
    private final PlanClient planClient;
    private final UserClient userClient;
    private final ProgressClient progressClient;


    /**
     * 获取指定学员所具有的培训计划列表
     *
     * @param student_id 学生id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getPlanListByStuId(int student_id, int page_size, int offset) {
        JSONObject req = userClient.getUserAccountByUid(student_id);
        if (Objects.equals(req.get("code"), 5005)) {
            return new DataFailRespond("该学生不存在！");
        }
        //获取指定学生的计划列表
        List<Integer> PlanIdList = trainingMapper.getPIdByStuId(student_id);


        List<PlanResult> list = new ArrayList<>();

        for (Integer i : PlanIdList) {
            JSONObject res = progressClient.getLessonPersentList(i, student_id, 999999, 0).join();
            List<LessonIdAndPersent> progressList =  res.getJSONArray("data").toJavaList(LessonIdAndPersent.class);
            double persentSum = progressList.stream().mapToDouble(LessonIdAndPersent::getPresent).sum();
            double x = persentSum / progressList.size();
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.HALF_UP); // 设置四舍五入模式为四舍五入
            df.setMinimumFractionDigits(2); // 设置小数位数
            String formattedAverage = df.format(x); // 格式化平均值
            double roundedAverage = Double.parseDouble(formattedAverage);
            Plan p = getPlanById(i);
            PlanResult result = new PlanResult(i, p.getTraining_title(), p.getTraining_purpose(), p.getTraining_start_time(), p.getTraining_end_time(), p.getDept_id(), p.getTraining_state(), roundedAverage, p.getExtra());
            list.add(result);
        }
        int endIndx = Math.min(offset + page_size, list.size());
        List<PlanResult> results = list.subList(offset, endIndx);
        return new DataPagingSuccessRespond("已成功获取该员工的培训计划列表", list.size(), results);
    }


    /**
     * 获取指定培训计划的课程列表
     *
     * @param student_id 学生id
     * @param plan_id    计划id
     * @return 根据处理结果返回对应消息
     * 2023/11/7
     */
    @Override
    public DataRespond getLessonByPIdAndStuId(int plan_id, int student_id, int page_size, int offset) {
        //判断存在性
        String StuMark = judgeStuExit(student_id);
        if (!StuMark.isBlank()) {
            return new DataFailRespond(StuMark);
        }
        String PlanMark = judgePlanExit(plan_id);
        if (!PlanMark.isBlank()) {
            return new DataFailRespond(PlanMark);
        }
        String Mark = judgeStuInPlan(student_id, plan_id);
        if (!Mark.isBlank()) {
            return new DataFailRespond(Mark);
        }
        JSONObject res = progressClient.getLessonPersentList(plan_id, student_id, 999999, 0).join();
        List<LessonIdAndPersent> progressList =  res.getJSONArray("data").toJavaList(LessonIdAndPersent.class);

        List<Integer> lessonIdList = trainingMapper.getLessonIdListByPId(plan_id);
        List<LessonResult> lessonResults = new ArrayList<>();
        for (Integer i : lessonIdList) {
            JSONObject req = planClient.getLessonInfo(i);
            JSONObject data = req.getJSONObject("data");
            String lessonName = data.getString("lesson_name");
            String lessonDes = data.getString("lesson_des");
            String lessonState = data.getString("lesson_state");
            Integer teacherId = data.getInteger("teacher_id");
            TeacherInfo teacherInfo = getTeaInfo(teacherId);
            ProgressLesson lessonProgress = trainingMapper.getLessonProgressByLIdAndStuId(i, student_id);
            double x = 0;
            if (lessonProgress != null) {

                x = progressList.stream().filter(obj -> Objects.equals(obj.getLesson_id(), i))
                        .map(LessonIdAndPersent::getPresent)
                        .findFirst().orElse(0.00);
            }
            LessonResult lessonResult = new LessonResult(i, lessonName, lessonDes, lessonState, x, teacherId, teacherInfo);
            lessonResults.add(lessonResult);
        }
        int endIndx = Math.min(offset + page_size, lessonResults.size());
        List<LessonResult> results = lessonResults.subList(offset, endIndx);
        return new DataPagingSuccessRespond("已成功获取此培训计划下的课程列表", lessonResults.size(), results);
    }


    /**
     * 获取指定课程下的章节列表
     *
     * @param student_id 学生id
     * @param lesson_id  计划id
     * @return 根据处理结果返回对应消息
     * 2023/11/7
     */
    @Override
    public DataRespond getChapterByStuIdAndLessId(int student_id, int lesson_id) {
        String StuMark = judgeStuExit(student_id);
        if (!StuMark.isBlank()) {
            return new DataFailRespond(StuMark);
        }
        String LessonMark = judgeLessonExit(lesson_id);
        if (!LessonMark.isBlank()) {
            return new DataFailRespond(LessonMark);
        }
        List<Chapter> allChapter = trainingMapper.getChapterByLessId(lesson_id);
        List<Integer> overChapterIdList = trainingMapper.getOverChapterIdByStuIdAndLessId(student_id, lesson_id);
        OverChapter realOverChapter = new OverChapter(0, null);
        List<ChapterResult> chapterResultList = new ArrayList<>();
        for (Chapter chapter : allChapter) {
            if (overChapterIdList.contains(chapter.getId())) {
                OverChapter overChapter = new OverChapter(chapter.getId(), chapter.getChapter_name());
                ChapterResult chapterResult = new ChapterResult(chapter.getId(), chapter.getChapter_name(), true);
                realOverChapter = overChapter;
                chapterResultList.add(chapterResult);
            } else {
                ChapterResult chapterResult = new ChapterResult(chapter.getId(), chapter.getChapter_name(), false);
                chapterResultList.add(chapterResult);
            }
        }
        AllChapter chapter = new AllChapter();
        if (realOverChapter.getId() != 0){
            chapter.setOverChapterList(realOverChapter);
            chapter.setChapterResultList(chapterResultList);
        }else {
            chapter.setChapterResultList(chapterResultList);
        }
        return new DataSuccessRespond("已成功获取此课程的章节列表", chapter);
    }


    /**
     * 根据计划Id获取指定计划详细
     *
     * @param id 计划id
     * @return 根据处理结果返回对应消息
     */
    private Plan getPlanById(int id) {
        JSONObject req = planClient.getPlanInfoById(id);
        JSONObject data = req.getJSONObject("data");
        JSONObject table = data.getJSONObject("table");
        JSONObject dept = data.getJSONObject("deptInfo");

        String training_title = table.getString("training_title");
        String training_purpose = table.getString("training_purpose");
        String training_start_time = table.getString("training_start_time");
        String training_end_time = table.getString("training_end_time");
        String training_state = table.getString("training_state");
        int dept_id = dept.getInteger("id");
        String extra = table.getString("extra");
        return new Plan(training_title, training_purpose, training_start_time, training_end_time, dept_id, training_state, extra);
    }


    /**
     * 获取指定学生指定计划的进度
     *
     * @param id 计划id
     * @return 根据处理结果返回对应消息
     */
    private double getPresent(int id, int student_id) {
        List<Integer> lessonIdList = trainingMapper.getLessonIdByPId(id);
        double sum = 0;
        for (Integer i : lessonIdList) {
            ProgressLesson progressLesson = trainingMapper.getProLessonByLIdAndStuId(i, student_id);
            double x = ComputeUtil.getStuProgress(progressLesson);
            sum += x;
        }
        System.out.println(lessonIdList);
        System.out.println(lessonIdList.size());
        return sum/lessonIdList.size();
    }


    /**
     * 判断学生是否在该计划内
     *
     * @param student_id 学生id
     * @param plan_id    计划id
     * @return 根据处理结果返回对应消息
     */
    private String judgeStuInPlan(int student_id, int plan_id) {
        Integer Mark = trainingMapper.judgeExitStuInPlan(plan_id, student_id);
        if (Objects.equals(Mark, 0)) {
            return "该学生不在该计划内！";
        }
        return "";
    }


    /**
     * 根据id获取教师信息
     *
     * @param id 学生id
     * @return 根据处理结果返回对应消息
     */
    private TeacherInfo getTeaInfo(int id) {
        JSONObject req = userClient.getUserAccountByUid(id);
        JSONObject data = req.getJSONObject("data");
        String realName = data.getString("realName");
        String mobile = data.getString("mobile");
        return new TeacherInfo(realName, mobile);
    }


    /**
     * 判断学生是否存在
     *
     * @param student_id 学生id
     * @return 根据处理结果返回对应消息
     */
    private String judgeStuExit(int student_id) {
        JSONObject req = userClient.getUserAccountByUid(student_id);
        if (Objects.equals(req.get("code"), 5005)) {
            return "该学生不存在！";
        }
        return "";
    }


    /**
     * 判断计划是否存在
     *
     * @param plan_id 计划id
     * @return 根据处理结果返回对应消息
     */
    private String judgePlanExit(int plan_id) {
        JSONObject req = planClient.getPlanInfoById(plan_id);
        if (Objects.equals(req.get("code"), 5005)) {
            return "该计划不存在！";
        }
        return "";
    }


    private String judgeLessonExit(int lesson_id) {
        JSONObject req = planClient.getLessonInfo(lesson_id);
        if (Objects.equals(req.get("code"), 5005)) {
            return "该课程不存在";
        }
        return "";
    }


}
