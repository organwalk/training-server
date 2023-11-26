package com.training.learn.mapper;

import com.training.learn.entity.table.QuestionTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionMapper {
    //获取指定测试的所有试题id
    @Select("select id from t_learn_test_questions where test_id=#{test_id}")
    List<Integer> getIdByTestId(int test_id);
    //插入指定测定的试题
    @Insert("insert into t_learn_test_questions(question_content, importance_id, true_answer, test_id)values (#{req.question_content},#{req.importance_id},#{req.true_answer},#{req.test_id})")
    @Options(useGeneratedKeys = true, keyProperty = "req.id")
    void insertQuestion(@Param("req")QuestionTable req);

    //获取指定测试的一条试题ID
    @Select("select id from t_learn_test_questions where test_id=#{test_id} limit 1")
    Integer getQuestionByTestId(int test_id);

    //根据id获取试题详情
    @Select("select * from t_learn_test_questions where id=#{id}")
    QuestionTable getQuestionById(int id);
    //删除指定测试下的所有试题
    @Delete("delete from t_learn_test_questions where test_id = #{test_id}")
    void deleteQuestionByTestId(int test_id);
    //获取指定试题的正确答案
    @Select("select true_answer from t_learn_test_questions where id=#{id}")
    String getAnswerById(int id);
    //获取指定试题的重要程度
    @Select("select importance_id from t_learn_test_questions where id=#{id}")
    Integer getImportanceIdById(int id);
}
