package com.training.learn.mapper;

import com.training.learn.entity.table.TypeTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * by zhaozhifeng 11-20
 */
@Mapper
public interface TypeMapper {
    //获取指定重要程度的分数
    @Select("select score from t_learn_test_type where id=#{id}")
    Integer getScoreById(int id);
    @Select("select id, importance_type, weight, score  from t_learn_test_type")
    List<TypeTable> getTypeList();
}
