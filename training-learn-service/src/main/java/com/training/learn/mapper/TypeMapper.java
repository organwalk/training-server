package com.training.learn.mapper;

import com.training.learn.entity.table.TypeTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TypeMapper {
    //获取指定重要程度的分数
    @Select("select score from t_learn_test_type where id=#{id}")
    Integer getScoreById(int id);
    //获取指定重要程度信息
    @Select("select * from t_learn_test_type where id = #{id}")
    TypeTable getTypeById(int id);
}
