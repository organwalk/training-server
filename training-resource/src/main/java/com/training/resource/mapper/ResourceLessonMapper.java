package com.training.resource.mapper;

import com.training.resource.entity.result.ResourceLessonInfo;
import com.training.resource.entity.table.ResourceLessonTable;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Mapper
@CrossOrigin
public interface ResourceLessonMapper {
    // 检查是否存在相同课程资源记录
    @Select("select id from t_resource_lesson where chapter_id = #{chapter_id}")
    Integer selectResourceLessonIdByChapterId(Integer chapter_id);

    // 插入课程资源记录
    @Insert("insert into t_resource_lesson(lesson_id, teacher_id, chapter_id, resource_path, up_datetime, file_hash) " +
            "values (#{obj.lessonId}, #{obj.teacherId}, #{obj.chapterId}, #{obj.resourcePath}, #{obj.upDatetime}, #{obj.fileHash})")
    void insertLessonResource(@Param("obj") ResourceLessonTable obj);

    // 检查重传接口指定课程章节是否存在
    @Select("select id from t_resource_lesson where lesson_id = #{lesson_id} and teacher_id = #{teacher_id} and chapter_id = #{chapter_id}")
    Integer selectIdByReUpdateArgs(@Param("lesson_id") Integer lesson_id,
                                   @Param("teacher_id") Integer teacher_id,
                                   @Param("chapter_id") Integer chapter_id);

    // 获取旧文件路径
    @Select("select resource_path from t_resource_lesson where chapter_id = #{chapter_id}")
    String selectOldFilePath(@Param("chapter_id") Integer chapter_id);

    // 修改教材资源路径
    @Update("update t_resource_lesson set resource_path = #{resource_path}, up_datetime = #{up_datetime} " +
            "where lesson_id = #{lesson_id} and teacher_id = #{teacher_id} and chapter_id = #{chapter_id}")
    void updateLessonResourcePath(@Param("resource_path") String resourcePath,
                                  @Param("up_datetime") String up_datetime,
                                  @Param("lesson_id") Integer lessonId,
                                  @Param("teacher_id") Integer teacherId,
                                  @Param("chapter_id") Integer chapter_id);

    // 删除指定课程教材文件
    @Delete("delete from t_resource_lesson where chapter_id = #{chapter_id}")
    void deleteOneLessonResource(@Param("chapter_id") Integer chapter);

    // 检查删除指定教师课程下的所有教材文件接口指定课程是否存在
    @Select("select id from t_resource_lesson where lesson_id = #{lesson_id}")
    Integer selectIdByDeleteAllLessonArgs(@Param("lesson_id") Integer lesson_id);

    // 删除指定教师课程下的所有教材文件
    @Delete("delete from t_resource_lesson where teacher_id = #{teacher_id} and lesson_id = #{lesson_id}")
    void deleteAllLessonResource(@Param("teacher_id") Integer teacherId,
                                 @Param("lesson_id") Integer lessonId);

    // 检查是否具有相同哈希值的文件
    @Select("select resource_path from t_resource_lesson where file_hash = #{fileHash} limit 1")
    String selectPathByFileHash(String fileHash);

    // 检查具有文件路径的记录是否不止一条
    @Select("select COUNT(id) from t_resource_lesson where resource_path = #{resourcePath} limit 2")
    Integer selectPathIsOverTwo(String resourcePath);

    // 根据相关信息获取文件哈希值
    @Select("select file_hash from t_resource_lesson " +
            "where chapter_id = #{chapterId}")
    String selectFileHashByInfo(@Param("chapterId") Integer chapterId);

    // 根据课程教材ID获取文件路径
    @Select("select resource_path from t_resource_lesson where id = #{rlId}")
    String selectLessonPathById(Integer rlId);

    // 获取指定课程资源列表
    @Select("select id, chapter_id, up_datetime from t_resource_lesson where lesson_id = #{lessonId}")
    List<ResourceLessonInfo> selectResourceLessonList(Integer lessonId);

    // 获取指定资源ID
    @Select("select id from t_resource_lesson where lesson_id = #{lesson_id} and chapter_id = #{chapter_id}")
    Integer getLessonResourceId(@Param("lesson_id")Integer lessonId,
                                @Param("chapter_id")Integer chapterId);

    // 获取章节ID列表
    @Select("select chapter_id from t_resource_lesson where lesson_id = #{lessonId}")
    List<Integer> getChapterIdListByLessonId(Integer lessonId);
}
