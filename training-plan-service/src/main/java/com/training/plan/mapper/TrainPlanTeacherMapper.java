package com.training.plan.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TrainPlanTeacherMapper {
    //插入一条新的记录
    @Insert("insert into t_training_plan_teacher(training_plan_id,training_teacher_id) VALUES (#{training_plan_id},#{training_teacher_id})")
    void insertTrainPlanTeacher(@Param("training_plan_id")int training_plan_id,@Param("training_teacher_id") int training_teacher_id);
    //判断教师是否已经存在
    @Select("select Count(id) from t_training_plan_teacher where training_teacher_id=#{training_teacher_id}")
    Integer CheckTeaInForm(int training_teacher_id);
}
