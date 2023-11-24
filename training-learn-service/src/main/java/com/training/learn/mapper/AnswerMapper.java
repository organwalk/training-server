package com.training.learn.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AnswerMapper {
    //删除指定测试的学生答案记录
    @Delete("delete from t_learn_test_answer where test_id=#{test_id}")
    void deleteQuestionByTestId(int test_id);
    //插入指定测试的学生答案
    @Insert("insert into t_learn_test_answer(answer, student_id, test_id, create_datetime) VALUES(#{answer},#{student_id},#{test_id},#{create_datetime}) ")
    void insertAnswer(@Param("answer")String answer,@Param("student_id")int student_id,@Param("test_id")int test_id,@Param("create_datetime")String create_datetime);
}