package com.training.plan.mapper;


import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TrainPlanStudentMapper {
    //插入一个新的记录
    @Insert("<script>" +
            "INSERT INTO t_training_plan_student(training_student_id, training_plan_id) " +
            "VALUES " +
            "<foreach collection='studentIdList' item='studentId' separator=','>" +
            "(#{studentId}, #{training_plan_id})" +
            "</foreach>" +
            "</script>")
    void insertTrainPlanStudent(@Param("studentIdList") List<Integer> studentIdList,
                                @Param("training_plan_id") int training_plan_id);
    //判断学生是否已经存在
    @Select("select id from t_training_plan_student where training_student_id=#{training_student_id} and training_plan_id=#{training_plan_id}")
    Integer CheckStuInForm(@Param("training_student_id") int training_student_id,@Param("training_plan_id")int training_plan_id);
    //获取指定id的所有学生id
    @Select("select training_student_id from t_training_plan_student where training_plan_id = #{training_plan_id}")
    List<Integer> getAllStuId(int training_plan_id);
    //判断表中是否存在指定计划
    @Select("select COUNT(id) from t_training_plan_student where training_plan_id = #{training_plan_id}")
    Integer getPlanByPlanId(int training_plan_id);

    @Select("select COUNT(id) from t_training_plan_student where training_plan_id=#{training_plan_id}")
    Integer getPlanStuCount(int training_plan_id);
    //获取指定计划的所有id
    @Select("select id from t_training_plan_student where training_plan_id=#{training_plan_id}")
    List<Integer> getAllIdByPlanID(int training_plan_id);
    //根据id删除
    @Delete("delete from t_training_plan_student where training_student_id =#{s_id} and training_plan_id = #{p_id}")
    Integer DeleteStu(@Param("s_id") int studentId, @Param("p_id") int planId);

    @Select("select COUNT(training_student_id) from t_training_plan_student where training_plan_id =#{id}")
    Integer ExitJudge(int id);

    @Delete("delete from t_training_plan_student where training_plan_id=#{training_plan_id}")
    void DeleteStuByPlanId(int training_plan_id);

    @Select("select plan_id from t_progress_plan where lesson_id=#{lesson_id}")
    Integer getPlanIdByLessonId(Integer lesson_id);

    @Select("select training_plan_id from t_training_plan_student where training_student_id = #{studentId}")
    List<Integer> selectPlanIdListByStudentId(Integer studentId);


    


}
