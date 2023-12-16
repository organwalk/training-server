package com.training.plan.mapper;

import com.training.plan.entity.request.TestReq;
import com.training.plan.entity.table.TestTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * by zhaozhifeng 2023-10-31
 */
@Mapper
public interface TestMapper {
    //获取指定课程id的所有测试题目
    @Select("select * from t_lesson_test where resource_lesson_id=#{resource_lesson_id}")
    List<TestTable> getAllTestByRLId(int resource_lesson_id);
    //获取指定课程id的题目数量
    @Select("select COUNT(id) from t_lesson_test where resource_lesson_id=#{resource_lesson_id}")
    Integer getCountByRLId(int resource_lesson_id);

    @Select("select * from t_lesson_test where id=#{id}")
    TestTable getTestById(int id);

    @Delete("delete from t_lesson_test where id = #{id}")
    Integer deleteById(int id);

    @Delete("delete from t_lesson_test where resource_lesson_id =#{resource_lesson_id}")
    Integer deleteByRLId(int resource_lesson_id);

    @Select("select COUNT(id) from t_lesson_test where resource_lesson_id=#{resource_lesson_id}")
    Integer judgeExit(int resource_lesson_id);

    @Update("update t_lesson_test set test_title=#{req.test_title},test_options_a=#{req.test_options_a},test_options_b=#{req.test_options_b},test_options_c=#{req.test_options_c},test_options_d=#{req.test_options_d},test_options_answer=#{req.test_options_answer},test_time=#{req.test_time},test_state=#{test_state} where id=#{id}")
    Integer updateTest(@Param("req")TestReq req,@Param("test_state")String test_state,@Param("id")int id);
}
