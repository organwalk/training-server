package com.training.progress.servier.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.MsgRespond;
import com.training.progress.client.PlanClient;
import com.training.progress.client.UserClient;
import com.training.progress.config.ToolConfig;
import com.training.progress.entity.request.ProgressChapterLessonReq;
import com.training.progress.entity.result.Chapter;
import com.training.progress.mapper.ProgressChapterMapper;
import com.training.progress.mapper.ProgressLessonMapper;
import com.training.progress.servier.ProgressService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     *  标记课程章节为已完成的具体实现
     * @param lesson_id 课程id
     * @param chapter_id 章节id
     * @param student_id 学生id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond MarkChapterComplete(int lesson_id, int chapter_id, int student_id) {
        //判断学生，课程，章节是否存在
        String StuExitMark = judgeStuExit(student_id);
        if (!StuExitMark.isBlank()){
            return MsgRespond.fail(StuExitMark);
        }
        String LessonExit = judgeLessonExit(lesson_id);
        if (!LessonExit.isBlank()){
            return MsgRespond.fail(LessonExit);
        }
        String ChapterExit = judgeChapterExit(lesson_id,chapter_id);
        if (!ChapterExit.isBlank()){
            return MsgRespond.fail(ChapterExit);
        }
        //判断是否已经标记完成
        Integer ProChapterExit = chapterMapper.judgeExitInTable(chapter_id,lesson_id,student_id);
        if (!Objects.equals(ProChapterExit,0)){
            return MsgRespond.fail("该学生本章课程已标记完成!");
        }
        String nowTime = ToolConfig.getTime();
        ProgressChapterLessonReq progressChapterLessonReq = new ProgressChapterLessonReq(lesson_id,student_id,chapter_id,nowTime);
        Integer i = chapterMapper.insertChapterCompletion(progressChapterLessonReq);
        if (i>0){
            lessonMapper.updateChapterSum(lesson_id,student_id);
        }
        return MsgRespond.success("已成功标记该章节为完成");
    }
    /**
     *  根据id判断学生是否存在
     * @param id 学生id
     * @return 根据处理结果返回对应消息
     */
    private String judgeStuExit(int id){
        JSONObject req = userClient.getUserAccountByUid(id);
        if (Objects.equals(req.get("code"),5005)){
            return "该学生不存在！";
        }
        return "";
    }
    /**
     *  根据id判断课程是否存在
     * @param id 课程id
     * @return 根据处理结果返回对应消息
     */
    private String judgeLessonExit(int id){
        JSONObject req = planClient.getLessonInfo(id);
        if(Objects.equals(req.get("code"),5005)){
            return "该课程不存在！";
        }
        return "";
    }

    private String judgeChapterExit(int id,int chapter_id){
        JSONArray req = planClient.getAllChapterByLessonId(id);
        List<Chapter> chapters = JSONArray.parseArray(req.toJSONString(),Chapter.class);
        boolean found = false;
        for (Chapter chapter:chapters){
            if (Objects.equals(chapter.getId(),chapter_id)){
               found=true;
               break;
            }
        }
        return found?"":"该章节不存在";
    }
}
