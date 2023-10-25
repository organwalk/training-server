package com.training.plan.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TrainPlanStudentMapper {
    //插入一个新的记录
    @Insert("insert into t_training_plan_student(training_student_id, training_plan_id) VALUES (#{training_student_id},#{training_plan_id})")
    void insertTrainPlanStudent(@Param("training_student_id") int training_student_id,@Param("training_plan_id")int training_plan_id);
    //判断学生是否已经存在
    @Select("select id from t_training_plan_student where training_student_id=#{training_student_id}")
    Integer CheckStuInForm(int training_student_id);
}
