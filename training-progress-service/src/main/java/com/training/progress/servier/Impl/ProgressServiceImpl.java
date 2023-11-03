package com.training.progress.servier.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.*;
import com.training.progress.client.PlanClient;
import com.training.progress.client.UserClient;
import com.training.progress.config.ToolConfig;
import com.training.progress.entity.request.ProgressChapterLessonReq;
import com.training.progress.entity.respond.StuChapterAll;
import com.training.progress.entity.result.Chapter;
import com.training.progress.entity.result.ChapterList;
import com.training.progress.entity.table.ProgressChapter;
import com.training.progress.mapper.ProgressChapterMapper;
import com.training.progress.mapper.ProgressLessonMapper;
import com.training.progress.servier.ProgressService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional
public class ProgressServiceImpl implements ProgressService {
    private final ProgressLessonMapper lessonMapper;
    private final ProgressChapterMapper chapterMapper;
    private final PlanClient planClient;
    private final UserClient userClient;


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
        String LessonExit = judgeLessonExit(lesson_id);
        if (!LessonExit.isBlank()) {
            return MsgRespond.fail(LessonExit);
        }
        String ChapterExit = judgeChapterExit(lesson_id, chapter_id);
        if (!ChapterExit.isBlank()) {
            return MsgRespond.fail(ChapterExit);
        }
        //判断是否已经标记完成
        Integer ProChapterExit = chapterMapper.judgeExitInTable(chapter_id, lesson_id, student_id);
        if (!Objects.equals(ProChapterExit, 0)) {
            return MsgRespond.fail("该学生本章课程已标记完成!");
        }
        String nowTime = ToolConfig.getTime();
        ProgressChapterLessonReq progressChapterLessonReq = new ProgressChapterLessonReq(lesson_id, student_id, chapter_id, nowTime);
        Integer i = chapterMapper.insertChapterCompletion(progressChapterLessonReq);
        Integer j = 0;
        if (i > 0) {
            j = lessonMapper.updateChapterSum(lesson_id, student_id);
        }
        return j > 0 ? MsgRespond.success("已成功标记该章节为完成") : MsgRespond.fail("标记失败！");
    }


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


    @Override
    public DataRespond getStuAllByLessonId(int lesson_id, int page_size, int offset) {
        String LessonMark = judgeLessonExit(lesson_id);
        if (!LessonMark.isBlank()) {
            return new DataFailRespond("该课程不存在！");
        }
        List<Integer> Stulist = chapterMapper.getAllStuIdByLessonId(lesson_id);
        List<StuChapterAll> alls = new ArrayList<>();
        for (Integer i : Stulist) {
            ProgressChapter progressChapter = chapterMapper.getProChapByStuId(i);
            Chapter chapter = getChapter(lesson_id, progressChapter.getOver_chapter_id());
            StuChapterAll stuChapterAll = new StuChapterAll(lesson_id, progressChapter.getStudent_id(), progressChapter.getOver_chapter_id(), chapter, progressChapter.getCompletion_date());
            alls.add(stuChapterAll);
        }
        int endIndx = Math.min(offset + page_size, alls.size());
        List<StuChapterAll> result = alls.subList(offset, endIndx);
        return new DataPagingSuccessRespond("已成功获取学员进度列表", alls.size(), result);
    }


    /**
     * 获取培训计划进度列表
     *
     * @param page_size
     * @param offset
     * @return
     */
    @Override
    public DataRespond getAllPlanProgressList(int page_size, int offset) {
        return null;
    }





    @Override
    public DataRespond getChapterListByStuIdAndLessonId(int student_id, int lesson_id) {
        String StuExitMark = judgeStuExit(student_id);
        if (!StuExitMark.isBlank()){
            return new DataFailRespond("该学生不存在!");
        }
        String LessonExitMark = judgeLessonExit(lesson_id);
        if (!LessonExitMark.isBlank()){
            return new DataFailRespond("该课程不存在！");
        }
        List<Integer> chapterList = chapterMapper.getChapterListByStuIdAndLessonId(student_id,lesson_id);
        return new DataSuccessRespond("已成功返回该学员所学最新章节及其已学章节列表",new ChapterList(chapterList.size(),chapterList));
    }










    /**
     * 根据id判断学生是否存在
     *
     * @param id 学生id
     * @return 根据处理结果返回对应消息
     */
    private String judgeStuExit(int id) {
        JSONObject req = userClient.getUserAccountByUid(id);
        if (Objects.equals(req.get("code"), 5005)) {
            return "该学生不存在！";
        }
        return "";
    }


    /**
     * 根据id判断课程是否存在
     *
     * @param id 课程id
     * @return 根据处理结果返回对应消息
     */
    private String judgeLessonExit(int id) {
        JSONObject req = planClient.getLessonInfo(id);
        if (Objects.equals(req.get("code"), 5005)) {
            return "该课程不存在！";
        }
        return "";
    }


    /**
     * 根据id判断章节是否存在
     *
     * @param id         章节id
     * @param chapter_id 章节id
     * @return 根据处理结果返回对应消息
     */
    private String judgeChapterExit(int id, int chapter_id) {
        JSONObject req = planClient.getAllChapterByLessonId(id);
        List<Chapter> chapters = JSONArray.parseArray(req.toJSONString(), Chapter.class);
        boolean found = false;
        for (Chapter chapter : chapters) {
            if (Objects.equals(chapter.getId(), chapter_id)) {
                found = true;
                break;
            }
        }
        return found ? "" : "该章节不存在";
    }


    /**
     * 根据id获取所有章节是否存在
     *
     * @param id 章节id
     * @return 根据处理结果返回对应消息
     */
    private List<Chapter> getChapterList(int id) {
        JSONObject list = planClient.getAllChapterByLessonId(id);
        JSONArray req = JSON.parseObject(String.valueOf(list)).getJSONArray("data");
        return JSONArray.parseArray(req.toJSONString(), Chapter.class);
    }


    private Chapter getChapter(int id, int chapter_id) {
        JSONObject list = planClient.getAllChapterByLessonId(id);
        JSONArray req = JSON.parseObject(String.valueOf(list)).getJSONArray("data");
        List<Chapter> chapters = JSONArray.parseArray(req.toJSONString(), Chapter.class);
        Chapter trachapter = null;
        for (Chapter chapter : chapters) {
            if (chapter.getId() == chapter_id) {
                trachapter = chapter;
                break;
            }
        }
        return trachapter;
    }


}
