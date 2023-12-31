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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * by linguowei 2023-11-06
 */
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
        Integer sumMark = trainingMapper.countTrainingStudentId(student_id);
        if (Objects.isNull(sumMark)){
            return new DataFailRespond("未能获取到培训计划列表");
        }

        //获取指定学生的计划列表
        List<Integer> PlanIdList = trainingMapper.getPIdByStuId(student_id, page_size, offset);
        List<PlanResult> list = new ArrayList<>();

        for (Integer i : PlanIdList) {
            JSONObject res = progressClient.getLessonPersentList(i, student_id, 999999, 0).join();
            if (Objects.equals(res.getInteger("code"), 5005)){
                return new DataFailRespond(res.getString("msg"));
            }
            List<LessonIdAndPersent> progressList =  res.getJSONArray("data").toJavaList(LessonIdAndPersent.class);
            double persentSum = progressList.stream().mapToDouble(LessonIdAndPersent::getPresent).sum();
            double x = persentSum / progressList.size();
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.HALF_UP); // 设置四舍五入模式为四舍五入
            df.setMinimumFractionDigits(2); // 设置小数位数
            String formattedAverage = df.format(x); // 格式化平均值
            double roundedAverage = Double.parseDouble(formattedAverage);
            Plan p = getPlanById(i);
            if (Objects.isNull(p)){
                return new DataFailRespond("培训管理服务异常，无法正常获取培训计划信息");
            }
            PlanResult result = new PlanResult(i, p.getTraining_title(), p.getTraining_purpose(), p.getTraining_start_time(), p.getTraining_end_time(), p.getDept_id(), p.getTraining_state(), roundedAverage, p.getExtra());
            list.add(result);
        }
        return new DataPagingSuccessRespond("已成功获取该员工的培训计划列表", sumMark, list);
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
        String Mark = judgeStuInPlan(student_id, plan_id);
        if (!Mark.isBlank()) {
            return new DataFailRespond(Mark);
        }

        Integer sumMark = trainingMapper.countLessonId(plan_id);
        if (Objects.isNull(sumMark)){
            return new DataFailRespond("未能成功获取课程列表");
        }

        JSONObject res;
        res = progressClient.getLessonPersentList(plan_id, student_id, 999999, 0).join();
        if (Objects.equals(res.getInteger("code"), 5005)){
            return new DataFailRespond(res.getString("msg"));
        }
        List<LessonIdAndPersent> progressList =  res.getJSONArray("data").toJavaList(LessonIdAndPersent.class);

        List<Integer> lessonIdList = trainingMapper.getLessonIdListByPId(plan_id, page_size, offset);
        List<LessonResult> lessonResults = new ArrayList<>();
        for (Integer i : lessonIdList) {
            JSONObject infoRes = planClient.getLessonInfo(i);
            if (Objects.equals(infoRes.getInteger("code"), 5005)){
                return new DataFailRespond(infoRes.getString("msg"));
            }
            JSONObject data = infoRes.getJSONObject("data");
            String lessonName = data.getString("lesson_name");
            String lessonDes = data.getString("lesson_des");
            String lessonState = data.getString("lesson_state");
            Integer teacherId = data.getInteger("teacher_id");
            TeacherInfo teacherInfo = getTeaInfo(teacherId);
            if (Objects.isNull(teacherInfo)){
                return new DataFailRespond("用户服务异常，无法正常获取用户信息");
            }
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

        return new DataPagingSuccessRespond("已成功获取此培训计划下的课程列表", sumMark, lessonResults);
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
        JSONObject res = planClient.getPlanInfoById(id);
        if (Objects.equals(res.getInteger("code"), 5005)){
            return null;
        }
        JSONObject data = res.getJSONObject("data");
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
     * 判断学生是否在该计划内
     *
     * @param student_id 学生id
     * @param plan_id    计划id
     * @return 根据处理结果返回对应消息
     */
    private String judgeStuInPlan(int student_id, int plan_id) {
        Integer Mark = trainingMapper.judgeExitStuInPlan(plan_id, student_id);
        if (Objects.equals(Mark, 0)) {
            return "未能获取到课程列表";
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
        JSONObject res = userClient.getUserAccountByUid(id);
        if (Objects.equals(res.getInteger("code"), 5005)){
            return null;
        }
        JSONObject data = res.getJSONObject("data");
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
        JSONObject res = userClient.getUserAccountByUid(student_id);
        if (Objects.equals(res.get("code"), 5005)) {
            return res.getString("msg");
        }
        return "";
    }


    private String judgeLessonExit(int lesson_id) {
        JSONObject res = planClient.getLessonInfo(lesson_id);
        if (Objects.equals(res.get("code"), 5005)) {
            return res.getString("msg");
        }
        return "";
    }


}
