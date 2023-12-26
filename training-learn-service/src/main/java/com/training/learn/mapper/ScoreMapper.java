package com.training.learn.mapper;

import com.training.learn.entity.result.AvgScore;
import com.training.learn.entity.table.ScoreTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * by zhaozhifeng 2023-11-20
 * by organwalk 2023-12-07
 */
@Mapper
public interface ScoreMapper {
    //判断该学生指定课程的成绩是否已经存在
    @Select("select id from t_learn_test_score where test_id=#{test_id} and student_id=#{student_id}")
    Integer judgeExitByTestIdAndStuId(@Param("test_id")int test_id,@Param("student_id")int student_id);

    //插入学生成绩
    @Insert("insert into t_learn_test_score(composite_score, must_type_composite_score, important_type_composite_score, normal_type_composite_score, student_id, test_id)values (#{composite_score},#{must_type_composite_score},#{important_type_composite_score},#{normal_type_composite_score},#{student_id},#{test_id})")
    void insertScore(@Param("composite_score")double composite_score,@Param("must_type_composite_score")double must_type_composite_score,@Param("important_type_composite_score")double important_type_composite_score,@Param("normal_type_composite_score")double normal_type_composite_score,@Param("student_id")int student_id,@Param("test_id")int test_id);

    //获取指定测试和指定学生的成绩
   @Select("select id, composite_score, must_type_composite_score, important_type_composite_score, normal_type_composite_score, student_id, test_id from t_learn_test_score where test_id=#{test_id} and student_id=#{student_id}")
   ScoreTable getByTestIdAndStuId(@Param("test_id")int test_id,@Param("student_id")int student_id);

    //获取指定学生在指定测试里的排名
    @Select("select composite_score from t_learn_test_score where test_id = #{test_id} " +
            "GROUP BY composite_score  ORDER BY composite_score desc")
    List<Integer> getCompositeScoreRank(Integer test_id);
    //获取指定测试的成绩列表
    @Select("select count(id) from t_learn_test_score where test_id = #{testId}")
    Integer countAllScoreByTestId(Integer testId);
    @Select("select * from t_learn_test_score where test_id=#{test_id} limit #{pageSize} offset #{offset}")
    List<ScoreTable> getAllScoreByTestId(@Param("test_id") int test_id,
                                         @Param("pageSize") Integer pageSize,
                                         @Param("offset") Integer offset);

    @Select("select AVG(composite_score) as avg_com_score, " +
            "AVG(must_type_composite_score) as avg_must_com_score, " +
            "AVG(important_type_composite_score) as avg_imp_com_score, AVG(normal_type_composite_score) as avg_nor_com_score " +
            "from t_learn_test_score where test_id = #{testId}")
    AvgScore getAvgCompositeScoreByTestId(Integer testId);
}
