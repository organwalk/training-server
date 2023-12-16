package com.training.progress.mapper;


import com.training.progress.entity.request.ProgressChapterLessonReq;
import com.training.progress.entity.table.ProgressChapter;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * by zhaozhifeng 2023-11-01
 */
@Mapper
public interface ProgressChapterMapper {
    //插入数据
    @Insert("insert into t_progress_chapter(lesson_id, student_id, over_chapter_id, completion_date) VALUES (#{req.lesson_id},#{req.student_id},#{req.over_chapter_id},#{req.completion_date})")
    Integer insertChapterCompletion(@Param("req") ProgressChapterLessonReq req);

    @Update("update t_progress_chapter set over_chapter_id=#{over_chapter_id},completion_date=#{completion_date} where lesson_id=#{lesson_id} and student_id=#{student_id}")
    Integer CompletionChapter(@Param("over_chapter_id") int over_chapter_id, @Param("completion_date") String completion_date, @Param("lesson_id") int lesson_id, @Param("student_id") int student_id);

    @Select("select COUNT(id) from t_progress_chapter where student_id=#{student_id} and lesson_id=#{lesson_id}")
    Integer getStuComChapSum(@Param("student_id") int student_id, @Param("lesson_id") int lesson_id);

    @Select("select COUNT(id) from t_progress_chapter where over_chapter_id=#{over_chapter_id} and lesson_id=#{lesson_id} and student_id=#{student_id}")
    Integer judgeExitInTable(@Param("over_chapter_id") int over_chapter_id, @Param("lesson_id") int lesson_id, @Param("student_id") int student_id);

    @Select("select id, lesson_id, student_id, over_chapter_id, completion_date from t_progress_chapter where lesson_id=#{lesson_id} limit #{page_size} offset #{offset}")
    List<ProgressChapter> getStuProChatByLessId(@Param("lesson_id") int lesson_id, @Param("page_size") int page_size, @Param("offset") int offset);

    @Select("select id, lesson_id, student_id, over_chapter_id, completion_date " +
            "from t_progress_chapter " +
            "where lesson_id=#{lesson_id} ORDER BY id DESC limit #{pageSize} offset #{offset}")
    List<ProgressChapter> getProChapByStuId(@Param("lesson_id") int lesson_id,
                                            @Param("pageSize") int pageSize,
                                            @Param("offset") int offset);

    @Select("select count(id) from t_progress_chapter where lesson_id=#{lesson_id}")
    Integer countId(@Param("lesson_id") int lesson_id);

    @Select("select over_chapter_id from t_progress_chapter where student_id=#{student_id} and lesson_id=#{lesson_id}")
    List<Integer> getChapterListByStuIdAndLessonId(@Param("student_id") int student_id, @Param("lesson_id") int lesson_id);
}
