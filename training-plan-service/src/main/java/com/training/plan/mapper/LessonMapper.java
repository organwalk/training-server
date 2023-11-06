package com.training.plan.mapper;

import com.training.plan.entity.request.LessonReq;
import com.training.plan.entity.request.LessonUpdate;
import com.training.plan.entity.table.LessonTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LessonMapper {
    //添加课程
    @Insert("insert into t_lesson(lesson_name, lesson_des, teacher_id, lesson_state) values(#{req.lesson_name},#{req.lesson_des},#{req.teacher_id},#{req.lesson_state}) ")
    Integer insertLesson(@Param("req")LessonReq req);
    //根据课程名获取课程id
    @Select("select id from t_lesson where lesson_name = #{lesson_name}")
    Integer getIdByL_Name(String lesson_name);

    @Select("select count(id) from t_lesson where teacher_id =#{teacher_id}")
    Integer getTeaLessCount(int teacher_id);

    @Select("select * from t_lesson where teacher_id=#{teacher_id} limit #{page_size} offset #{offset}")
    List<LessonTable> getTeaAllLess(@Param("teacher_id")int teacher_id,@Param("page_size")int page_size,@Param("offset")int offset);

    @Select("select * from t_lesson where id = #{id}")
    LessonTable getLessInfoById(int id);

    @Update("update t_lesson set lesson_name=#{req.lesson_name},lesson_des=#{req.lesson_des} where id=#{id} and teacher_id = #{teacher_id}")
    Integer updateLesson(@Param("req") LessonUpdate req, @Param("id")int id, @Param("teacher_id")int teacher_id);

    @Delete("delete from t_lesson where id = #{id}")
    Integer deleteLessonById(int id);

    @Delete("delete from t_lesson where teacher_id =#{teacher_id}")
    Integer deleteLessonByTId(int teacher_id);

    @Select("select COUNT(id) from t_lesson where teacher_id=#{teacher_id}")
    Integer getCountByTID(int teacher_id);

    @Update("update t_lesson set lesson_state=#{lesson_state} where id = #{id}")
    Integer updateState(@Param("lesson_state")int lesson_state,@Param("id")int id);

    @Select("select lesson_state from t_lesson where id = #{id}")
    Integer getState(int id);

    @Select("select id from t_lesson where teacher_id=#{teacher_id}")
    List<Integer> getTeaLessIdList(int teacher_id);

    @Select("select lesson_id from t_progress_plan where plan_id=#{plan_id}")
    List<Integer> getLIDByPId(int plan_id);

}
