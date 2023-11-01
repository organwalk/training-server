package com.training.progress.mapper;


import com.training.progress.entity.request.ProgressChapterLessonReq;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProgressChapterMapper {
    @Insert("insert into t_progress_chapter(lesson_id, student_id, over_chapter_id, completion_date) VALUES (#{req.lesson_id},#{req.student_id},#{req.over_chapter_id},#{req.completion_date})")
    Integer insertChapterCompletion(@Param("req") ProgressChapterLessonReq req);

    @Update("update t_progress_chapter set over_chapter_id=#{over_chapter_id},completion_date=#{completion_date} where lesson_id=#{lesson_id} and student_id=#{student_id}")
    Integer CompletionChapter(@Param("over_chapter_id")int over_chapter_id,@Param("completion_date")String completion_date,@Param("lesson_id")int lesson_id,@Param("student_id")int student_id);

    @Select("select COUNT(id) from t_progress_chapter where student_id=#{student_id} and lesson_id=#{lesson_id}")
    Integer getStuComChapSum(@Param("student_id")int student_id,@Param("lesson_id")int lesson_id);

    @Select("select COUNT(id) from t_progress_chapter where over_chapter_id=#{over_chapter_id} and lesson_id=#{lesson_id} and student_id=#{student_id}")
    Integer judgeExitInTable(@Param("over_chapter_id")int over_chapter_id,@Param("lesson_id")int lesson_id,@Param("student_id")int student_id);
}
