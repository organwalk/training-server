package com.training.learn.mapper;

import com.training.learn.entity.table.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * by zhaozhifeng 2023-11-10
 * by organwlak 2023-12-09
 */
@Mapper
public interface CommentMapper {
    @Insert("insert into t_learn_comment(user_id, lesson_id,content,create_datetime)values (#{user_id},#{lesson_id},#{content},#{create_datetime})")
    Integer insertCommentOne(@Param("user_id")int user_id,@Param("lesson_id")int lesson_id,@Param("content")String content,@Param("create_datetime")String create_datetime);

    @Insert("insert into t_learn_comment(user_id, lesson_id, chapter_id, content,create_datetime)values (#{user_id},#{lesson_id},#{chapter_id},#{content},#{create_datetime})")
    Integer insertCommentTwo(@Param("user_id")int user_id,@Param("lesson_id")int lesson_id,@Param("chapter_id")int chapter_id,@Param("content")String content,@Param("create_datetime")String create_datetime);


    @Select("select COUNT(id) from t_learn_comment where id=#{id}")
    Integer judgeCommentExit(int id);

    @Select("select id, user_id, lesson_id, chapter_id, content, create_datetime from t_learn_comment where lesson_id=#{lesson_id} and chapter_id IS NULL ORDER BY id DESC")
    @Results(id="commentResultsMap",value ={
            @Result(column = "id",property = "id"),
            @Result(column = "user_id",property = "userId"),
            @Result(column = "lesson_id",property = "lessonId"),
            @Result(column = "chapter_id",property = "chapterId"),
            @Result(column = "content",property = "content"),
            @Result(column = "create_datetime",property = "createDatetime")
    })
    List<Comment> getCommentList(int lesson_id);


    @Select("select lesson_id from t_learn_comment where id=#{id}")
    Integer getLessonIdByCommentId(int id);


    @Select("select id, user_id, lesson_id, chapter_id, content, create_datetime from t_learn_comment " +
            "where lesson_id=#{lesson_id} and chapter_id=#{chapter_id} ORDER BY id DESC limit #{pageSize} offset #{offset}")
    @Results(id="commentResultMap",value ={
            @Result(column = "id",property = "id"),
            @Result(column = "user_id",property = "userId"),
            @Result(column = "lesson_id",property = "lessonId"),
            @Result(column = "chapter_id",property = "chapterId"),
            @Result(column = "content",property = "content"),
            @Result(column = "create_datetime",property = "createDatetime")
    })
    List<Comment> getCommentByLessonIdAndChapterId(@Param("lesson_id")int lesson_id,
                                                   @Param("chapter_id")int chapter_id,
                                                   @Param("pageSize") int pageSize,
                                                   @Param("offset") int offset);
    @Select("select count(id) from t_learn_comment " +
            "where lesson_id=#{lesson_id} and chapter_id=#{chapter_id}")
    Integer countComment(@Param("lesson_id")int lesson_id, @Param("chapter_id")int chapter_id);

    @Delete("delete from t_learn_comment where id=#{id}")
    Integer deleteCommentById(int id);

    @Select("select id, user_id, lesson_id, chapter_id, content, create_datetime from t_learn_comment where id=#{id}")
    Comment getCommentById(int id);
}
