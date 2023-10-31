package com.training.plan.mapper;


import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TrainPlanStudentMapper {
    //插入一个新的记录
    @Insert("insert into t_training_plan_student(training_student_id, training_plan_id) VALUES (#{training_student_id},#{training_plan_id})")
    void insertTrainPlanStudent(@Param("training_student_id") int training_student_id,@Param("training_plan_id")int training_plan_id);
    //判断学生是否已经存在
    @Select("select id from t_training_plan_student where training_student_id=#{training_student_id}")
    Integer CheckStuInForm(int training_student_id);
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
    @Delete("delete from t_training_plan_student where id =#{id}")
    Integer DeleteStu(int id);

    @Select("select COUNT(training_student_id) from t_training_plan_student where id =#{id}")
    Integer ExitJudge(int id);

    @Delete("delete from t_training_plan_student where training_plan_id=#{training_plan_id}")
    void DeleteStuByPlanId(int training_plan_id);

    


}
