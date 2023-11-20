package com.training.learn.mapper;

import com.training.learn.entity.table.ScoreTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScoreMapper {
    //判断该学生指定课程的成绩是否已经存在
    @Select("select id from t_learn_test_score where test_id=#{test_id} and student_id=#{student_id}")
    Integer judgeExitByTestIdAndStuId(@Param("test_id")int test_id,@Param("student_id")int student_id);
    //删除指定课程的所有成绩
    @Delete("delete from t_learn_test_score where test_id=#{test_id}")
    void deleteByTestId(int test_id);
    //插入学生成绩
    @Insert("insert into t_learn_test_score(composite_score, must_type_composite_score, important_type_composite_score, normal_type_composite_score, student_id, test_id)values (#{composite_score},#{must_type_composite_score},#{important_type_composite_score},#{normal_type_composite_score},#{student_id},#{test_id})")
    void insertScore(@Param("composite_score")double composite_score,@Param("must_type_composite_score")double must_type_composite_score,@Param("important_type_composite_score")double important_type_composite_score,@Param("normal_type_composite_score")double normal_type_composite_score,@Param("student_id")int student_id,@Param("test_id")int test_id);

    //获取指定测试和指定学生的成绩
   @Select("select * from t_learn_test_score where test_id=#{test_id} and student_id=#{student_id}")
   ScoreTable getByTestIdAndStuId(@Param("test_id")int test_id,@Param("student_id")int student_id);

    //获取指定学生在指定测试里的排名
    @Select("SELECT COUNT(*) + 1 score_rank FROM t_learn_test_score WHERE composite_score > (SELECT composite_score FROM t_learn_test_score WHERE test_id = #{test_id} AND student_id = #{student_id})")
    int getCompositeScoreRank(int test_id, int student_id);
    //获取指定测试的成绩列表
    @Select("select * from t_learn_test_score where test_id=#{test_id}")
    List<ScoreTable> getAllScoreByTestId(int test_id);
    //获取指定测试的总分的平均数
    @Select("SELECT AVG(composite_score) FROM t_learn_test_score WHERE test_id = #{test_id}")
    double getComposite_scoreAVGByTestId(int test_id);
    //获取指定测试的必须类别题综合得分的平均数
    @Select("select  AVG(must_type_composite_score) from t_learn_test_score where test_id=#{test_id}")
    double getMustCompositeScoreAVGByTestId(int test_id);
    //获取指定测试的重要类别题综合得分的平均数
    @Select("select  AVG(important_type_composite_score) from t_learn_test_score where test_id=#{test_id}")
    double getImportanceCompositeScoreAVGByTestId(int test_id);
    //获取指定测试的一般类别题综合得分的平均数
    @Select("select  AVG(normal_type_composite_score) from t_learn_test_score where test_id=#{test_id}")
    double getNormalCompositeScoreAVGByTestId(int test_id);
}
