package com.training.plan.mapper;

import com.training.plan.entity.request.TestReq;
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
    //获取计划列表
    @Select("select * from t_training_plan limit #{page_size} offset #{offset}")
    List<TrainingPlanTable> getAllPlan(@Param("page_size") int page_size,@Param("offset") int offset);
    //获取列表总数
    @Select("select COUNT(id) from t_training_plan")
    Integer getPlanCount();
    //获取指定部门计划列表
    @Select("select * from t_training_plan where dept_id = #{dept_id} limit #{page_size} offset #{offset}")
    List<TrainingPlanTable> getDeptAllPlan(@Param("dept_id")int dept_id,@Param("page_size")int page_size,@Param("offset")int offset);
    //获取指定部门计划列表总数
    @Select("select COUNT(id) from t_training_plan where dept_id =#{deot_id}")
    Integer getDeptPlanCount(int dept_id);
    //通过计划id获取计划详细信息
    @Select("select * from t_training_plan where id = #{id}")
    TrainingPlanTable getTrainById(int id);




    //在视频教材插入测试题,使用param注解给传入的参数命名实现同时传入对象和其他参数
    @Insert("insert into t_lesson_test (test_title, test_options_a, test_options_b, test_options_c, test_options_d, test_options_answer, test_time, resource_lesson_id, test_state) values (#{testReq.test_title},#{testReq.test_options_a},#{testReq.test_options_b},#{testReq.test_options_c},#{testReq.test_options_d},#{testReq.test_options_answer},#{testReq.test_time},#{lesson_id},#{test_state})")
    Integer insertTest(@Param("testReq") TestReq testReq,@Param("lesson_id")int lesson_id,@Param("test_state")String test_state);

    //通过视频id检查存在多少个测试题
    @Select("select count(id) from t_lesson_test where resource_lesson_id=#{lesson_id}")
    Integer getTestSub(int lesson_id);
}
