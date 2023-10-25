package com.training.plan.mapper;

import com.training.plan.entity.request.TrainingPlanReq;
import com.training.plan.entity.table.TrainingPlanTable;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TrainingPlanMapper {
    //插入一个计划数据
    @Insert("insert into t_training_plan(training_title,training_purpose,training_start_time,training_end_time,dept_id,training_state)values (#{req.training_title},#{req.training_purpose},#{req.training_start_time},#{req.training_end_time},#{req.dept_id},#{req.training_state})")
    void insertTrainingPlan(@Param("req") TrainingPlanReq req);
    //检查计划是否已经存在
    @Select("select id from t_training_plan where training_title=#{trainingTitle}")
    Integer selectTrainingTitleExist(String trainingTitle);

    @Select("select * from t_training_plan limit #{page_size} offset #{offset}")
    List<TrainingPlanTable> getAllPlan(@Param("page_size") int page_size,@Param("offset") int offset);

    @Select("select COUNT(id) from t_training_plan")
    Integer getPlanCount();

    @Select("select * from t_training_plan where dept_id = #{dept_id} limit #{page_size} offset #{offset}")
    List<TrainingPlanTable> getDeptAllPlan(@Param("dept_id")int dept_id,@Param("page_size")int page_size,@Param("offset")int offset);

    @Select("select COUNT(id) from t_training_plan where dept_id =#{deot_id}")
    Integer getDeptPlanCount(int dept_id);


}
