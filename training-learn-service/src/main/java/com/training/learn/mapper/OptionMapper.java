package com.training.learn.mapper;

import com.training.learn.entity.table.OptionsTable;
import org.apache.ibatis.annotations.*;

/**
 * by zhaozhifeng 2023-11-20
 */
@Mapper
public interface OptionMapper {
    //插入指定问题的选项信息
    @Insert("insert into t_learn_test_options(`option`, isMore, question_id) VALUES (#{option},#{isMore},#{question_id})")
    void insertOption(@Param("option")String option,@Param("isMore")int isMore,@Param("question_id")int question_id);
    //获取指定问题的选项信息
    @Select("select * from t_learn_test_options where question_id=#{question_id}")
    OptionsTable getOptionByQuesId(int question_id);
    //删除指定问题的选项信息
    @Delete("delete from t_learn_test_options where question_id=#{question_id}")
    void deleteByQuesId(int question_id);
}
