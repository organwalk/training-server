package com.training.plan.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.DataSuccessRespond;
import com.training.common.entity.MsgRespond;
import com.training.plan.client.ResourceClient;
import com.training.plan.client.ProgressClient;
import com.training.plan.entity.table.ChapterTable;
import com.training.plan.entity.table.LessonTable;
import com.training.plan.mapper.ChapterMapper;
import com.training.plan.mapper.LessonMapper;
import com.training.plan.reposoty.LessonCache;
import com.training.plan.service.ChapterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * by zhaozhifeng 2023-10-31
 */
@Service
@AllArgsConstructor
@Transactional
public class ChapterServiceImpl implements ChapterService {
    private final ChapterMapper chapterMapper;
    private final LessonCache lessonCache;
    private final LessonMapper lessonMapper;
    private final ProgressClient progressClient;
    private final ResourceClient resourceClient;
    /**
     *  添加课程章节
     * @param name 章节名称
     * @param lesson_id 课程id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond insertChapter(String name, int lesson_id) {
        //判断是否存在该课程
        LessonTable lessonTable = lessonMapper.getLessInfoById(lesson_id);
        if (lessonTable==null){
            return MsgRespond.fail("该课程不存在");
        }
        //判断章节是否存在
        Integer ExitMark = chapterMapper.getIdByCName(name,lesson_id);
        if (!Objects.equals(ExitMark,0)){
            return MsgRespond.fail("该章节已经存在");
        }
        JSONObject jsonObject = JSON.parseObject(name);
        String chapter_name = jsonObject.getString("chapter_name");
        Integer i = chapterMapper.insertChapter(chapter_name,lesson_id);
        lessonCache.deleteChapter(String.valueOf(lesson_id));
        if(i>0){
            Integer sum = chapterMapper.getCountByLId(lesson_id);
            JSONObject res = progressClient.updateChapterSum(sum,lesson_id);
            if (Objects.equals(res.getInteger("code"), 5005)){
                return MsgRespond.fail(res.getString("msg"));
            }
        }
        return i>0?MsgRespond.success("已成功添加此课程章节"):MsgRespond.fail("添加失败！");
    }
    /**
     * 获取指定课程的所有章节
     * @param lessonId 课程id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getChapterLessonId(int lessonId) {
        //判断是否存在该章节
        Integer Mark = chapterMapper.getCountByLId(lessonId);
        if (Objects.equals(Mark,0)){
            return new DataFailRespond("该课程没有章节");
        }
        //判断缓存中是否有该课程的章节
        String key = String.valueOf(lessonId);
        if (lessonCache.getChapter(key)!= null){
            String result = (String) lessonCache.getChapter(key);
            List<ChapterTable> lessonTables = JSON.parseArray(result, ChapterTable.class);
            return new DataSuccessRespond("已成功获取此课程的章节列表",lessonTables);
        }
        List<ChapterTable> list = chapterMapper.getChapterByLessonId(lessonId);
        if (list!=null){
            lessonCache.saveChapter(key,list);
        }
        return new DataSuccessRespond("已成功获取此课程的章节列表",list);
    }
    /**
     *  编辑指定章节名称
     * @param chapter_name 章节名称
     * @param id 章节id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond updateChapterName(String chapter_name, int id) {
        //判断是否存在该章节
        Integer Mark = chapterMapper.JudgeChapterExit(id);
        if (Objects.equals(Mark,0)){
            return MsgRespond.fail("该课程下没有该章节");
        }
        JSONObject jsonObject = JSON.parseObject(chapter_name);
        String name = jsonObject.getString("chapter_name");
        ChapterTable chapterTable = chapterMapper.getChapterByID(id);
        Integer i = chapterMapper.updateChapterName(name,id);
        //判断修改后的章节名与之前是否一致，一致则删除缓存
        if(!Objects.equals(chapterTable.getChapterName(),name)){
            String key = String.valueOf(chapterMapper.getLessonIdByChapterId(id));
            lessonCache.deleteChapter(key);
        }
        return i>0?MsgRespond.success("已成功编辑此章节名称"):MsgRespond.fail("编辑失败！");
    }
    /**
     *  根据id删除指定章节
     * @param chapterId 章节id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond deleteChapterById(int chapterId) {
        //判断是否存在该章节
        Integer Mark = chapterMapper.JudgeChapterExit(chapterId);
        if (Objects.equals(Mark,0)){
            return MsgRespond.fail("该课程下没有该章节");
        }

        Integer lessonId = chapterMapper.getLessonIdByChapterId(chapterId);

        // 删除章节教材资源
        JSONObject res = resourceClient.deleteOneLessonResource(chapterId);
        if (Objects.equals(res.getInteger("code"), 5005)){
            return MsgRespond.fail(res.getString("msg"));
        }

        //删除缓存
        lessonCache.deleteChapter(String.valueOf(lessonId));

        //删除章节
        Integer i = chapterMapper.deleteChapterById(chapterId);
        if (i == 0){
            return MsgRespond.fail("删除失败");
        }

        return MsgRespond.success("已成功删除此章节");
    }

    @Override
    public DataRespond getChapterDetail(Integer chapterId) {
        ChapterTable chapterTable = chapterMapper.getChapterByID(chapterId);
        return Objects.isNull(chapterTable)
                ? new DataFailRespond("未能获取到章节信息")
                : new DataSuccessRespond("已成功获取章节信息", chapterTable);
    }


}
