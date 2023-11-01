package com.training.progress.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProgressLessonMapper {


    @Select("select over_chapter_sum = over_chapter_sum+1 from t_progress_lesson where lesson_id=#{lesson_id} and student_id=#{student_id}")
    Integer updateChapterSum(@Param("lesson_id")int lesson_id,@Param("student_id")int student_id);

    //获取培训计划进度列表()开发中
    //@Select("")
}
