package com.training.plan.mapper;

import com.training.plan.entity.table.TrainingPlanTeacherTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TrainPlanTeacherMapper {
    //插入一条新的记录
    @Insert("insert into t_training_plan_teacher(training_plan_id,training_teacher_id) VALUES (#{training_plan_id},#{training_teacher_id})")
    void insertTrainPlanTeacher(@Param("training_plan_id")int training_plan_id,@Param("training_teacher_id") int training_teacher_id);
    //判断教师是否已经存在
    @Select("select Count(id) from t_training_plan_teacher where training_teacher_id=#{training_teacher_id}")
    Integer CheckTeaInForm(int training_teacher_id);
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
    @Delete("delete from t_training_plan_teacher where id = #{id}")
    Integer deleteByTId(int id);
    //判断指定教师是否在该计划内
    @Select("select COUNT(training_teacher_id) from t_training_plan_teacher where id=#{id}")
    Integer ExitJudge(int id);

}
