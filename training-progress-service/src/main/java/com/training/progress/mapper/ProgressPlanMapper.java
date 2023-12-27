package com.training.progress.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * by zhaozhifeng 2023-11-06
 * by organwalk 2023-12-11
 */
@Mapper
public interface ProgressPlanMapper {

    @Insert("insert into t_progress_plan(plan_id, teacher_id, lesson_id) VALUES (#{plan_id},#{teacher_id},#{lesson_id})")
    Integer insertToProgressPlan(@Param("plan_id") int plan_id, @Param("teacher_id") int teacher_id, @Param("lesson_id") Integer lesson_id);

    @Select("select lesson_id from t_progress_plan where teacher_id=#{teacher_id} and plan_id = #{plan_id} order by id")
    List<Integer> getLessonIdByTeaId(@Param("plan_id")int plan_id,
                                     @Param("teacher_id")int teacher_id);

    @Select("select plan_id from t_progress_plan GROUP BY plan_id")
    List<Integer> getAllPlanId();

    @Select("select lesson_id from t_progress_plan where plan_id = #{plan_id}")
    List<Integer> getLessonIdByPlanId(int plan_id);

    // 获取指定培训计划下教师总数
    @Select("select count(teacher_id) from t_progress_plan where plan_id = #{plan_id}")
    Integer countTeacher(Integer plan_id);
    @Select("SELECT t.lesson_id FROM t_progress_plan t INNER JOIN t_progress_lesson o ON t.lesson_id = o.lesson_id " +
            "WHERE t.plan_id = #{plan_id}  GROUP BY t.lesson_id")
    List<Integer> getCommonLessonIdsByPlanId(int plan_id);

    @Select("select count(id) from t_progress_plan where plan_id=#{plan_id}")
    Integer judgePlanExit(@Param("plan_id") int plan_id);

    @Select("select lesson_id from t_progress_plan where plan_id=#{plan_id} LIMIT #{page_size} offset #{offset}")
    List<Integer> getLesson(@Param("plan_id")int plan_id,@Param("page_size")int page_size,@Param("offset")int offset);

    @Select("select COUNT(id) from t_progress_plan where plan_id=#{plan_id}")
    Integer getCountByPlanId(int plan_id);
}