package com.training.progress.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProgressPlanMapper {

    @Insert("insert into t_progress_plan(plan_id, teacher_id, lesson_id) VALUES (#{plan_id},#{teacher_id},#{lesson_id})")
    Integer insertToProgressPlan(@Param("plan_id") int plan_id, @Param("teacher_id") int teacher_id, @Param("lesson_id") int lesson_id);

    @Select("select lesson_id from t_progress_plan where teacher_id=#{teacher_id}")
    List<Integer> getLessonIdByTeaId(int teacher_id);

    @Select("select plan_id from t_progress_plan")
    List<Integer> getAllPlanId();

    @Select("select lesson_id from t_progress_plan where plan_id = #{plan_id}")
    List<Integer> getLessonIdByPlanId(int plan_id);

    @Select("select id from t_progress_plan where plan_id=#{plan_id}")
    Integer judgePlanExit(int plan_id);

    @Select("select lesson_id from t_progress_plan where plan_id=#{plan_id} LIMIT #{page_size} offset #{offset}")
    List<Integer> getLesson(@Param("plan_id")int plan_id,@Param("page_size")int page_size,@Param("offset")int offset);

    @Select("select COUNT(id) from t_progress_plan where plan_id=#{plan_id}")
    Integer getCountByPlanId(int plan_id);


}