package com.training.progress.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.*;

import com.training.progress.client.PlanClient;
import com.training.progress.client.UserClient;
import com.training.progress.config.RabbitMqConfig;
import com.training.progress.config.ToolConfig;
import com.training.progress.entity.msg.MarkChapterMsg;
import com.training.progress.entity.request.ProgressChapterLessonReq;
import com.training.progress.entity.respond.*;
import com.training.progress.entity.result.Chapter;
import com.training.progress.entity.result.ChapterList;
import com.training.progress.entity.result.PlanPresent;
import com.training.progress.entity.table.ProgressChapter;
import com.training.progress.entity.table.ProgressLesson;
import com.training.progress.mapper.ProgressChapterMapper;
import com.training.progress.mapper.ProgressLessonMapper;
import com.training.progress.mapper.ProgressPlanMapper;
import com.training.progress.producer.EventProcessMsgProducer;
import com.training.progress.service.ProgressService;
import com.training.progress.utils.ComputeUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

/**
 * by zhaozhifeng 2023-11-06
 * by organwalk 2023-12-04
 */
@Service
@AllArgsConstructor
@Transactional
public class ProgressServiceImpl implements ProgressService {
    private final ProgressLessonMapper lessonMapper;
    private final ProgressChapterMapper chapterMapper;
    private final ProgressPlanMapper planMapper;
    private final PlanClient planClient;
    private final UserClient userClient;
    private final EventProcessMsgProducer eventProcessMsgProducer;


    /**
     * 标记课程章节为已完成的具体实现
     *
     * @param lesson_id  课程id
     * @param chapter_id 章节id
     * @param student_id 学生id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond MarkChapterComplete(int lesson_id, int chapter_id, int student_id) {
        //判断学生，课程，章节是否存在
        String StuExitMark = judgeStuExit(student_id);
        if (!StuExitMark.isBlank()) {
            return MsgRespond.fail(StuExitMark);
        }

        String ChapterExit = judgeChapterExit(lesson_id, chapter_id);
        if (!ChapterExit.isBlank()) {
            return MsgRespond.fail(ChapterExit);
        }

        eventProcessMsgProducer.triggerMarkChapter(
                new MarkChapterMsg(
                        String.valueOf(UUID.randomUUID()),
                        lesson_id,
                        chapter_id,
                        student_id,
                        ToolConfig.getTime()
                )
        );

        return MsgRespond.success("已提交标记处理");
    }


    /**
     * 设置学员的课程总体进度
     *
     * @param lesson_id          课程id
     * @param over_chapter_sum   完成章节总数
     * @param lesson_chapter_sum 该课程总章节数
     * @param student_id         学生id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond insertStuLessonProgress(int lesson_id, int student_id, int over_chapter_sum, int lesson_chapter_sum) {
        //判断学生，课程是否存在
        String StuExitMark = judgeStuExit(student_id);
        if (!StuExitMark.isBlank()) {
            return MsgRespond.fail(StuExitMark);
        }
        String LessonExit = judgeLessonExit(lesson_id);
        if (!LessonExit.isBlank()) {
            return MsgRespond.fail(LessonExit);
        }
        Integer ExitMark = lessonMapper.judgeExitInTable(lesson_id, student_id);
        if (!Objects.equals(ExitMark, 0)) {
            return MsgRespond.fail("该学生已经在表中");
        }
        Integer i = lessonMapper.insertPorLesson(lesson_id, student_id, over_chapter_sum, lesson_chapter_sum);
        return i > 0 ? MsgRespond.success("已成功设置学员课程总体进度") : MsgRespond.fail("设置学员课程总体进度失败！");
    }


    /**
     * 获取学员在指定课程下学习到的章节列表
     *
     * @param lesson_id 课程id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getStuAllByLessonId(int lesson_id, int page_size, int offset) {
        Integer countMark = chapterMapper.countId(lesson_id);
        if (Objects.isNull(countMark) || countMark == 0) {
            return new DataFailRespond("未能获取到该课程的记录");
        }

        List<StuChapterAll> result = new ArrayList<>();
        List<ProgressChapter> progressChapters = chapterMapper.getProChapByStuId(lesson_id,page_size,offset);
        for (ProgressChapter progressChapter : progressChapters) {
            Chapter chapter = getChapter(lesson_id, progressChapter.getOver_chapter_id());
            if (Objects.isNull(chapter)){
                return new DataFailRespond("培训管理服务异常，无法正常获取指定课程的所有章节");
            }
            StuChapterAll stuChapterAll = new StuChapterAll(lesson_id, progressChapter.getStudent_id(), progressChapter.getOver_chapter_id(), chapter, progressChapter.getCompletion_date());
            result.add(stuChapterAll);
        }

        return new DataPagingSuccessRespond("已成功获取学员进度列表", countMark, result);
    }


    /**
     * 获取所有学生在指定课程的进度
     *
     * @param lesson_id 课程id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getAllStuLessonPresent(int lesson_id, int page_size, int offset) {
        String ExitMark = judgeLessonExit(lesson_id);
        if (!ExitMark.isBlank()) {
            return new DataFailRespond("该课程不存在！");
        }
        List<ProgressLesson> progressLessonList = lessonMapper.getAllProLessByLessonId(lesson_id, page_size, offset);
        List<StuPresent> presents = new ArrayList<>();
        for (ProgressLesson progressLesson : progressLessonList) {
            double present = ComputeUtil.getStuProgress(progressLesson);
            StuPresent stuPresent = new StuPresent(lesson_id, progressLesson.getStudent_id(), present);
            presents.add(stuPresent);
        }
        return new DataPagingSuccessRespond("已成功获取该课程下的百分比进度数据列表", presents.size(), presents);
    }


    /**
     * 建立课程进度跟踪
     *
     * @param plan_id    计划id
     * @param lesson_id  课程id
     * @param teacher_id 教师id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond insertProgressPlan(int plan_id, Integer lesson_id, int teacher_id) {
        String Mark = judgePlanExit(plan_id);
        if (!Mark.isBlank()) {
            return MsgRespond.fail("该计划不存在!");
        }

        Integer i = planMapper.insertToProgressPlan(plan_id, teacher_id, lesson_id);
        return i > 0 ? MsgRespond.success("已成功建立课程跟踪机制") : MsgRespond.fail("创建失败！");
    }


    /**
     * 获取指定教师的课程进度
     *
     * @param teacher_id 教师id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getTeaAllPresent(int planId, int teacher_id, String auth, String username) {
        String name1 = getName(teacher_id);
        if (name1.isBlank()){
            return new DataFailRespond("用户服务异常，无法正常获取用户账号信息");
        }
        if (Objects.equals(auth, "teacher") && !Objects.equals(username, name1)) {
            return new DataFailRespond("只能查看自己的课程进度信息");
        }
        String Mark = judgeStuExit(teacher_id);
        if (!Mark.isBlank()) {
            return new DataFailRespond(Mark);
        }

        // 获取课程ID列表
        List<Integer> LessonIdList = planMapper.getLessonIdByTeaId(planId, teacher_id);
        // 根据课程ID计算其进度
        List<Lesson_Present> lesson_presents = new ArrayList<>();
        double all_total_progresss = 0;
        for (Integer i : LessonIdList) {
            // 获取所有学生的课程进度
            List<ProgressLesson> progressLessonList = lessonMapper.getAllProgressByLessonId(i);

            // 计算单个课程进度
            double lessonPresent = ComputeUtil.comProgress(progressLessonList);

            all_total_progresss += lessonPresent;

            String name = getLessonById(i);
            if (name.isBlank()){
                return new DataFailRespond("培训管理服务异常，无法正常获取课程信息");
            }
            if (Double.isNaN(lessonPresent)) {
                lessonPresent = 0;
            }

            Lesson_Present lesson_present = new Lesson_Present(i, name, lessonPresent);
            lesson_presents.add(lesson_present);
        }
        List<Lesson_Present> result = reorder(lesson_presents);

        double all_total_progress = all_total_progresss / LessonIdList.size();

        if (Double.isNaN(all_total_progress)) {
            all_total_progress = 0;
        }
        TeaPresent teaPresent = new TeaPresent(all_total_progress, result);
        return new DataSuccessRespond("已成功获取百分比进度列表", teaPresent);
    }


    /**
     * 获取培训计划进度列表
     *
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getAllPlanPresent(int page_size, int offset) {
        List<PlanPresent> planPresents = new ArrayList<>();
        List<Integer> PlanIdList = planMapper.getAllPlanId();
        for (Integer i : PlanIdList) {
            Integer teacherSum = planMapper.countTeacher(i);
            // 获取课程ID列表
            List<Integer> lessonIdList = planMapper.getLessonIdByPlanId(i);
            double allPresent = 0;
            // 获取每个课程的进度求值所需信息
            for (Integer j : lessonIdList) {
                List<ProgressLesson> progressLessonList = lessonMapper.getAllProgressByLessonId(j);
                // 进行计算
                double present = ComputeUtil.comProgress(progressLessonList);
                System.out.println(present);
                allPresent += present;
            }
            if (Double.isNaN(allPresent)) {
                allPresent = 0;
            }
            PlanPresent planPresent = new PlanPresent(i, (allPresent / lessonIdList.size()) / teacherSum);
            planPresents.add(planPresent);
        }
        int endIndx = Math.min(offset + page_size, planPresents.size());
        List<PlanPresent> result = planPresents.subList(offset, endIndx);
        return new DataPagingSuccessRespond("已获取培训计划进度列表", planPresents.size(), result);
    }


    /**
     * 获取指定培训计划下建立跟踪机制的课程ID列表
     *
     * @param plan_id 计划id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond gerLessonIdListByPlanId(int plan_id, int page_size, int offset) {
        Integer mark = planMapper.judgePlanExit(plan_id);
        if (Objects.equals(mark, 0)) {
            return new DataFailRespond("该计划不存在！");
        }
        Integer Count = planMapper.getCountByPlanId(plan_id);
        List<Integer> LessonList = planMapper.getLesson(plan_id, page_size, offset);
        return new DataPagingSuccessRespond("已成功获取该培训计划下的课程ID列表", Count, LessonList);
    }


    /**
     * 获取指定培训计划下建立跟踪机制的课程ID列表
     *
     * @param student_id 学生id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getAllLessonPresentByStuId(int student_id, int plan_id, int page_size, int offset) {
        List<Integer> LessonIdList = planMapper.getCommonLessonIdsByPlanId(plan_id);
        List<LessPresent> lessPresents = new ArrayList<>();
        for (Integer i : LessonIdList) {
            ProgressLesson progressLesson = lessonMapper.getByLessIdAndStuID(i, student_id);
            double x = ComputeUtil.getStuProgress(progressLesson);
            if (Double.isNaN(x)) {
                x = 0;
            }
            LessPresent lessPresent = new LessPresent(i, x);
            lessPresents.add(lessPresent);
        }
        int endIndx = Math.min(offset + page_size, lessPresents.size());
        List<LessPresent> result = lessPresents.subList(offset, endIndx);
        return new DataPagingSuccessRespond("已成功返回该学员的课程进度列表", lessPresents.size(), result);
    }


    /**
     * 获取学员在指定课程下学习进度百分比数据
     *
     * @param student_id 学生id
     * @param lesson_id  课程id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getChapterListByStuIdAndLessonId(int student_id, int lesson_id) {
        String StuExitMark = judgeStuExit(student_id);
        if (!StuExitMark.isBlank()) {
            return new DataFailRespond("该学生不存在!");
        }
        String LessonExitMark = judgeLessonExit(lesson_id);
        if (!LessonExitMark.isBlank()) {
            return new DataFailRespond("该课程不存在！");
        }
        List<Integer> chapterList = chapterMapper.getChapterListByStuIdAndLessonId(student_id, lesson_id);
        return new DataSuccessRespond("已成功返回该学员所学最新章节及其已学章节列表", new ChapterList(chapterList.size(), chapterList));
    }


    /**
     * 更新进度服务章节总数
     *
     * @param sum       章节总数
     * @param lesson_id 课程id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond updateChapterSum(Integer sum, Integer lesson_id) {
        String lessonMark = judgeLessonExit(lesson_id);
        if (!lessonMark.isBlank()) {
            return MsgRespond.fail(lessonMark);
        }
        lessonMapper.UpdateChapterSum(sum, lesson_id);
        return MsgRespond.success("修改成功！");
    }

    @Override
    public DataRespond getStudentIdList(Integer lessonId) {
        List<Integer> studentIdList = lessonMapper.getStudentIdList(lessonId);
        return studentIdList.isEmpty()
                ? new DataFailRespond("fail")
                : new DataSuccessRespond("success", studentIdList);
    }


    /**
     * 根据id判断学生是否存在
     *
     * @param id 学生id
     * @return 根据处理结果返回对应消息
     */
    private String judgeStuExit(int id) {
        JSONObject res = userClient.getUserAccountByUid(id);
        if (Objects.equals(res.get("code"), 5005)) {
            return res.getString("msg");
        }
        return "";
    }


    /**
     * 使列表按照一定顺序排序
     *
     * @return 根据处理结果返回对应消息
     */
    private List<Lesson_Present> reorder(List<Lesson_Present> lesson_presents) {
        Comparator<Lesson_Present> comparator = Comparator.comparingDouble(Lesson_Present::getTotal_progress);
        lesson_presents.sort(comparator);
        return lesson_presents;

    }


    /**
     * 根据id获取用户名
     *
     * @param id 用户id
     * @return 根据处理结果返回对应消息
     */
    private String getName(int id) {
        JSONObject res = userClient.getUserAccountByUid(id);
        if (Objects.equals(res.getInteger("code"), 5005)){
            return "";
        }
        JSONObject data = res.getJSONObject("data");
        return data.getString("username");
    }


    /**
     * 根据id判断课程是否存在
     *
     * @param id 课程id
     * @return 根据处理结果返回对应消息
     */
    private String judgeLessonExit(int id) {
        JSONObject res = planClient.getLessonInfo(id);
        if (Objects.equals(res.get("code"), 5005)) {
            return res.getString("msg");
        }
        return "";
    }


    private String getLessonById(int id) {
        JSONObject res = planClient.getLessonInfo(id);
        if (Objects.equals(res.getInteger("code"), 5005)){
            return "";
        }
        JSONObject data = res.getJSONObject("data");
        return data.getString("lesson_name");
    }


    /**
     * 根据id判断章节是否存在
     *
     * @param id         章节id
     * @param chapter_id 章节id
     * @return 根据处理结果返回对应消息
     */
    private String judgeChapterExit(int id, int chapter_id) {
        JSONObject res = planClient.getAllChapterByLessonId(id);
        if (res.getInteger("code") == 2002){
            List<Chapter> chapters = res.getJSONArray("data").toJavaList(Chapter.class);
            boolean found = false;
            for (Chapter chapter : chapters) {
                if (Objects.equals(chapter.getId(), chapter_id)) {
                    found = true;
                    break;
                }
            }
            return found ? "" : "该章节不存在";
        }else {
            return res.getString("msg");
        }
    }


    /**
     * 根据id获取章节
     *
     * @param id 章节id
     * @return 根据处理结果返回对应消息
     */

    private Chapter getChapter(int id, int chapter_id) {
        JSONObject res = planClient.getAllChapterByLessonId(id);
        if (Objects.equals(res.getInteger("code"), 5005)){
            return null;
        }
        List<Chapter> chapters = res.getJSONArray("data").toJavaList(Chapter.class);
        Chapter trachapter = null;
        for (Chapter chapter : chapters) {
            if (chapter.getId() == chapter_id) {
                trachapter = chapter;
                break;
            }
        }
        return trachapter;
    }


    /**
     * 根据id获取计划
     *
     * @param id 计划id
     * @return 根据处理结果返回对应消息
     */
    private String judgePlanExit(int id) {
        JSONObject res = planClient.getPlanByPlanId(id);
        if (Objects.equals(res.get("code"), 5005)) {
            return res.getString("msg");
        }
        return "";
    }


}
