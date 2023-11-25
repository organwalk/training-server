package com.training.learn.mapper;

import com.training.learn.entity.request.TestReq;
import com.training.learn.entity.table.Test;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TestMapper {
    //创建测试
    @Insert("insert into t_learn_test(test_title, lesson_id, teacher_id, start_datetime, end_datetime, create_datetime, isRelease) values(#{req.test_title},#{req.lesson_id},#{req.teacher_id},#{req.start_datetime},#{req.end_datetime},#{req.create_datetime},#{req.isRelease}) ")
    Integer creatTest(@Param("req")TestReq req);
    //判断测试名是否重复
    @Select("select * from t_learn_test where test_title=#{test_title}")
    Test judgeTitleExit(String test_title);
    //根据id获取测试
    @Select("select * from t_learn_test where id=#{id}")
    Test getTestById(int id);
    //设置指定id的测试为发布状态
    @Update("update t_learn_test set isRelease = 1 and create_datetime=#{create_datetime} where id=#{id}")
    void updateIsRelease(@Param("id")int id,@Param("create_datetime")String create_datetime);
    //获取指定课程和教师的测试
    @Select("select * from t_learn_test where lesson_id=#{lesson_id} and teacher_id=#{teacher_id} limit #{page_size} offset #{offset}")
    List<Test> getListByLessonIdAndTeacherId(@Param("lesson_id")int lesson_id,@Param("teacher_id")int teacher_id,@Param("page_size")int page_size,@Param("offset")int offset);

    //获取指定课程和教师的测试总数
    @Select("select COUNT(id) from t_learn_test where lesson_id=#{lesson_id} and teacher_id=#{teacher_id}")
    Integer getCountOfListByLessonIdAndTeacherId(@Param("lesson_id")int lesson_id,@Param("teacher_id")int teacher_id);
    //获取指定课程的所有测试id
    @Select("select id from t_learn_test where lesson_id=#{lesson_id}")
    List<Integer> getAllIdByLessonId(int lesson_id);
    //修改测试信息
    @Update("update  t_learn_test set test_title=#{test_title},start_datetime=#{start_datetime},end_datetime=#{end_datetime} where id=#{id}")
    void updateTest(@Param("test_title")String test_title,@Param("start_datetime")String start_datetime,@Param("end_datetime")String end_datetime,@Param("id")int id);
    //获取指定课程的所有测试
    @Select("select * from t_learn_test where lesson_id=#{lesson_id} limit #{page_size} offset #{offset}")
    List<Test> getAllTestByLessonId(@Param("lesson_id") int lesson_id,@Param("page_size")int page_size,@Param("offset")int offset);
    //获取指定课程的测试数目
    @Select("select COUNT(id) from t_learn_test where lesson_id=#{lesson_id}")
    Integer getCountByLessonId(int lesson_id);

    @Delete("delete from t_learn_test where id=#{id}")
    void deleteTestById(int id);

    // 查询发布状态
    @Select("select isRelease from t_learn_test where id = #{test_id}")
    Integer getReleaseState(int test_id);
}
