package com.training.resource.mapper;

import com.training.resource.entity.table.ResourceNoteTable;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ResourceNoteMapper {

    // 插入一条笔记保存记录
    @Insert("insert into t_resource_note(lesson_id, chapter_id, up_id, note_title, note_des, note_path, up_datetime) " +
            "values (#{obj.lessonId}, #{obj.chapterId}, #{obj.upId}, #{obj.noteTitle}, #{obj.noteDes}, #{obj.notePath}, #{obj.upDatetime})")
    @Options(useGeneratedKeys = true, keyProperty = "obj.id")
    void insertNote(@Param("obj")ResourceNoteTable obj);

    // 根据id获取笔记路径
    @Select("select note_path from t_resource_note where id = #{noteId}")
    String selectNotePathById(Integer noteId);
    // 删除一条笔记记录
    @Delete("delete from t_resource_note where id = #{noteId}")
    void deleteNoteById(Integer noteId);
    @Select("select count(id) from t_resource_note where chapter_id = #{chapterId}")
    Integer selectNoteIdCountByChapterId(Integer chapterId);
    // 根据章节ID删除笔记
    @Delete("delete from t_resource_note where chapter_id = #{chapterId}")
    void deleteNoteByChapterId(Integer chapterId);
    @Select("select count(id) from t_resource_note where lesson_id = #{lessonId}")
    Integer selectNoteIdCountByLessonId(Integer lessonId);
    // 根据课程ID删除笔记
    @Delete("delete from t_resource_note where lesson_id = #{lessonId}")
    void deleteNoteByLessonId(Integer lessonId);
    // 根据笔记ID获取笔记名
    @Select("select note_title from t_resource_note where id = #{noteId}")
    String selectNoteTitleById(Integer noteId);
}
