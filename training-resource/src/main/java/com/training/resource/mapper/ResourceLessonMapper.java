package com.training.resource.mapper;

import com.training.resource.entity.table.ResourceLessonTable;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ResourceLessonMapper {
    // 检查是否存在相同课程资源记录
    @Select("select id from t_resource_lesson where chapter_id = #{chapter_id}")
    Integer selectLessonIdByChapterId(Integer chapter_id);

    // 插入课程资源记录
    @Insert("insert into t_resource_lesson(lesson_id, teacher_id, chapter_id, resource_path, up_datetime) " +
            "values (#{obj.lessonId}, #{obj.teacherId}, #{obj.chapterId}, #{obj.resourcePath}, #{obj.upDatetime})")
    void insertLessonResource(@Param("obj") ResourceLessonTable obj);

    // 检查重传接口指定课程章节是否存在
    @Select("select id from t_resource_lesson where lesson_id = #{lesson_id} and teacher_id = #{teacher_id} and chapter_id = #{chapter_id}")
    Integer selectIdByReUpdateArgs(@Param("lesson_id") Integer lesson_id,
                                   @Param("teacher_id") Integer teacher_id,
                                   @Param("chapter_id") Integer chapter_id);

    // 获取旧文件路径
    @Select("select resource_path from t_resource_lesson where lesson_id = #{lesson_id} and teacher_id = #{teacher_id} and chapter_id = #{chapter_id}")
    String selectOldFilePath(@Param("lesson_id") Integer lesson_id,
                             @Param("teacher_id") Integer teacher_id,
                             @Param("chapter_id") Integer chapter_id);

    // 修改教材资源路径
    @Update("update t_resource_lesson set resource_path = #{resource_path}, up_datetime = #{up_datetime} " +
            "where lesson_id = #{lesson_id} and teacher_id = #{teacher_id} and chapter_id = #{chapter_id}")
    void updateLessonResourcePath(@Param("resource_path") String resourcePath,
                                  @Param("up_datetime") String up_datetime,
                                  @Param("lesson_id") Integer lessonId,
                                  @Param("teacher_id") Integer teacherId,
                                  @Param("chapter_id") Integer chapter_id);

    // 删除指定课程教材文件
    @Delete("delete from t_resource_lesson where teacher_id = #{teacher_id} and lesson_id = #{lesson_id} and chapter_id = #{chapter_id}")
    void deleteOneLessonResource(@Param("teacher_id") Integer teacherId,
                                 @Param("lesson_id") Integer lessonId,
                                 @Param("chapter_id") Integer chapter);

    // 检查删除指定教师课程下的所有教材文件接口指定课程是否存在
    @Select("select id from t_resource_lesson where lesson_id = #{lesson_id} and teacher_id = #{teacher_id}")
    Integer selectIdByDeleteAllLessonArgs(@Param("lesson_id") Integer lesson_id,
                                          @Param("teacher_id") Integer teacher_id);

    // 删除指定教师课程下的所有教材文件
    @Delete("delete from t_resource_lesson where teacher_id = #{teacher_id} and lesson_id = #{lesson_id}")
    void deleteAllLessonResource(@Param("teacher_id") Integer teacherId,
                                 @Param("lesson_id") Integer lessonId);
}
