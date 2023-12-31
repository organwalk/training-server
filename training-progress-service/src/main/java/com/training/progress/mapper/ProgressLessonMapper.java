package com.training.progress.mapper;

import com.training.progress.entity.table.ProgressLesson;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * by zhaozhifeng 2023-11-01
 * by organwalk 2023-12-03
 */
@Mapper
public interface ProgressLessonMapper {


    @Update("UPDATE t_progress_lesson SET over_chapter_sum = over_chapter_sum + 1 WHERE lesson_id = #{lesson_id} AND student_id = #{student_id}")
    Integer updateChapterSum(@Param("lesson_id")int lesson_id,@Param("student_id")int student_id);

    @Insert("insert into t_progress_lesson(lesson_id, student_id, over_chapter_sum, lesson_chapter_sum) VALUES (#{lesson_id},#{student_id},#{over_chapter_sum},#{lesson_chapter_sum})")
    Integer insertPorLesson(@Param("lesson_id")int lesson_id,@Param("student_id")int student_id,@Param("over_chapter_sum")int over_chapter_sum,@Param("lesson_chapter_sum")int lesson_chapter_sum);

    @Select("select COUNT(id) from t_progress_lesson where lesson_id=#{lesson_id} and student_id=#{student_id}")
    Integer judgeExitInTable(@Param("lesson_id")int lesson_id,@Param("student_id")int student_id);

    @Select("select student_id from t_progress_lesson where lesson_id=#{lesson_id}")
    Integer getStuIdByLessonId(int lesson_id);

    @Select("select id, lesson_id, student_id, over_chapter_sum, lesson_chapter_sum " +
            "from t_progress_lesson where lesson_id=#{lesson_id} order by over_chapter_sum desc limit #{pageSize} offset #{offset}")
    List<ProgressLesson> getAllProLessByLessonId(@Param("lesson_id") int lesson_id,
                                                 @Param("pageSize") int page_size,
                                                 @Param("offset") int offset);

    @Select("select id, lesson_id, student_id, over_chapter_sum, lesson_chapter_sum from t_progress_lesson where lesson_id=#{lesson_id}")
    List<ProgressLesson> getAllProgressByLessonId(int lesson_id);

    @Select("select * from t_progress_lesson where lesson_id=#{lesson_id} and student_id=#{student_id}")
    ProgressLesson getByLessIdAndStuID(@Param("lesson_id")int lesson_id,@Param("student_id")int student_id);

    @Update("update t_progress_lesson set lesson_chapter_sum = #{lesson_chapter_sum} where lesson_id=#{lesson_id}")
    void UpdateChapterSum(@Param("lesson_chapter_sum")Integer lesson_chapter_sum,@Param("lesson_id")Integer lesson_id);

    @Select("select student_id from t_progress_lesson where lesson_id = #{lessonId}")
    List<Integer> getStudentIdList(Integer lessonId);
}
