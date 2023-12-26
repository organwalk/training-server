package com.training.learn.mapper;


import com.training.learn.entity.respond.Chapter;
import com.training.learn.entity.result.ProgressLesson;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * by linguowei 2023-11-10
 */
@Mapper
public interface TrainingMapper {
    @Select("select count(id) from t_training_plan_student where training_student_id = #{training_student_id}")
    Integer countTrainingStudentId(@Param("training_student_id") Integer training_student_id);
    @Select("select training_plan_id from t_training_plan_student " +
            "where training_student_id=#{training_student_id} limit #{pageSize} offset #{offset}")
    List<Integer> getPIdByStuId(@Param("training_student_id") Integer training_student_id,
                                @Param("pageSize") Integer pageSize,
                                @Param("offset") Integer offset);

    @Select("select id from t_training_plan_student where training_plan_id=#{training_plan_id} and training_student_id=#{training_student_id}")
    Integer judgeExitStuInPlan(@Param("training_plan_id")int training_plan_id,@Param("training_student_id")int training_student_id);

    @Select("select count(lesson_id) from t_progress_plan where plan_id=#{plan_id}")
    Integer countLessonId(@Param("plan_id")Integer planId);
    @Select("select lesson_id from t_progress_plan where plan_id=#{plan_id} limit #{pageSize} offset #{offset}")
    List<Integer> getLessonIdListByPId(@Param("plan_id") Integer plan_id,
                                       @Param("pageSize") Integer pageSize,
                                       @Param("offset") Integer offset);

    @Select("select * from t_progress_lesson where lesson_id=#{lesson_id} and student_id=#{student_id}")
    ProgressLesson getLessonProgressByLIdAndStuId(@Param("lesson_id")int lesson_id,@Param("student_id")int student_id);

    @Select("select over_chapter_id from t_progress_chapter where student_id=#{student_id} and lesson_id=#{lesson_id}")
    List<Integer> getOverChapterIdByStuIdAndLessId(@Param("student_id")int student_id,@Param("lesson_id")int lesson_id);

    @Select("select * from t_lesson_chapter where lesson_id=#{lesson_id}")
    List<Chapter> getChapterByLessId(int lesson_id);
}
