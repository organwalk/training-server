package com.training.progress.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProgressLessonMapper {


    @Select("select over_chapter_sum = over_chapter_sum+1 from t_progress_lesson where lesson_id=#{lesson_id} and student_id=#{student_id}")
    Integer updateChapterSum(@Param("lesson_id")int lesson_id,@Param("student_id")int student_id);

    @Insert("insert into t_progress_lesson(lesson_id, student_id, over_chapter_sum, lesson_chapter_sum) VALUES (#{lesson_id},#{student_id},#{over_chapter_sum},#{lesson_chapter_sum})")
    Integer insertPorLesson(@Param("lesson_id")int lesson_id,@Param("student_id")int student_id,@Param("over_chapter_sum")int over_chapter_sum,@Param("lesson_chapter_sum")int lesson_chapter_sum);

    @Select("select COUNT(id) from t_progress_lesson where lesson_id=#{lesson_id} and student_id=#{student_id}")
    Integer judgeExitInTable(@Param("lesson_id")int lesson_id,@Param("student_id")int student_id);

    //获取培训计划进度列表()开发中
    //@Select("")
}
