package com.training.plan.mapper;


import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TrainPlanTeacherMapper {
    //插入一条新的记录
    @Insert("<script>" +
            "INSERT INTO t_training_plan_teacher (training_plan_id, training_teacher_id) " +
            "VALUES " +
            "<foreach collection='teacherIdList' item='teacherId' separator=','>" +
            "(#{training_plan_id}, #{teacherId})" +
            "</foreach>" +
            "</script>")
    void insertTrainPlanTeacher(@Param("training_plan_id") int training_plan_id,
                                @Param("teacherIdList") List<Integer> teacherIdList);
    //判断教师是否已经存在
    @Select("select Count(id) from t_training_plan_teacher where training_teacher_id=#{training_teacher_id} and training_plan_id=#{training_plan_id}")
    Integer CheckTeaInForm(@Param("training_teacher_id") int training_teacher_id,@Param("training_plan_id")int training_plan_id);
    //获取指定计划的所有老师id
    @Select("select training_teacher_id from t_training_plan_teacher where training_plan_id=#{training_plan_id} limit #{page_size} offset #{offset}")
    List<Integer> getAllTeacherId(@Param("training_plan_id") int training_plan_id,@Param("page_size")int page_size,@Param("offset")int offset);
    //获取指定计划的教师数量
    @Select("select COUNT(id) from t_training_plan_teacher where training_plan_id=#{training_plan_id}")
    Integer getCountOfTea(int training_plan_id);
    //获取指定计划的所有教师id
    @Select("select id from t_training_plan_teacher where training_plan_id = #{training_plan_id}")
    List<Integer> getAllTeaId(int training_plan_id);
    //删除指定id的教师
    @Delete("delete from t_training_plan_teacher where training_teacher_id = #{t_id} and training_plan_id = #{p_id}")
    Integer deleteByTId(@Param("t_id") int teacherId, @Param("p_id") int planId);
    //判断指定教师是否在该计划内
    @Select("select COUNT(training_teacher_id) from t_training_plan_teacher where training_plan_id=#{id}")
    Integer ExitJudge(int id);

    @Delete("delete from t_training_plan_teacher where training_plan_id=#{training_plan_id}")
    void deleteTeaByPlanId(int id);

}
