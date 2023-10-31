package com.training.plan.mapper;

import com.training.plan.entity.table.ChapterTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChapterMapper {
    @Insert("insert into t_lesson_chapter(chapter_name, lesson_id) VALUES (#{chapter_name},#{lesson_id})")
    Integer insertChapter(@Param("chapter_name")String chapter_name,@Param("lesson_id")int lesson_id);

    @Select("select COunt(id) from t_lesson_chapter where chapter_name = #{chapter_name} and lesson_id = #{lesson_id}")
    Integer getIdByCName(@Param("chapter_name") String chapter_name,@Param("lesson_id")int lesson_id);

    @Select(("select * from t_lesson_chapter where lesson_id = #{lessson_id}"))
    List<ChapterTable>  getChapterByLessonId(int lesson_id);

    @Select("select COUNT(id) from t_lesson_chapter where lesson_id = #{lesson_id}")
    Integer getCountByLId(int lesson_id);

    @Update("update t_lesson_chapter set chapter_name=#{chapter_name} where id = #{id}")
    Integer updateChapterName(@Param("chapter_name")String chapter_name,@Param("id")int id);

    @Select("select count(chapter_name) from t_lesson_chapter where id=#{id};")
    Integer JudgeChapterExit(int id);

    @Delete("delete from t_lesson_chapter where id = #{id}")
    Integer deleteChapterById(int id);

    @Select("select lesson_id from t_lesson_chapter where id=#{id}")
    Integer getLessonIdByChapterId(int id);

    @Delete("delete from t_lesson_chapter where lesson_id=#{lesson_id}")
    Integer deleteAllChapterByLessonId(int lesson_id);

    @Select("select * from t_lesson_chapter where id=#{id}")
    ChapterTable getChapterByID(int id);



}
