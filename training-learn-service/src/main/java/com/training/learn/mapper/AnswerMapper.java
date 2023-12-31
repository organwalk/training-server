package com.training.learn.mapper;

import org.apache.ibatis.annotations.*;

/**
 * by linguowei 2023-11-20
 * by organwalk 2023-12-07
 */
@Mapper
public interface AnswerMapper {
    //插入指定测试的学生答案
    @Insert("insert into t_learn_test_answer(answer, student_id, test_id, create_datetime) VALUES(#{answer},#{student_id},#{test_id},#{create_datetime}) ")
    void insertAnswer(@Param("answer")String answer,@Param("student_id")int student_id,@Param("test_id")int test_id,@Param("create_datetime")String create_datetime);

    @Select("select id from t_learn_test_answer where student_id = #{studentId} and test_id = #{testId}")
    Integer countOverTest(@Param("studentId") Integer studentId, @Param("testId") Integer testId);
}
