package com.training.learn.mapper;

import com.training.learn.entity.table.Reply;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * by zhaozhifeng 2023-11-10
 * by organwalk 2023-12-09
 */
@Mapper
public interface ReplyMapper {
    @Insert("insert into t_learn_reply(user_id, comment_id, content, create_datetime) " +
            "VALUES (#{obj.user_id},#{obj.comment_id},#{obj.content},#{obj.create_datetime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void replyComment(@Param("obj") Reply reply);

    @Select("select COUNT(id) from t_learn_reply where comment_id=#{comment_id} and user_id=#{user_id}")
    Integer judgeReplyExit(@Param("comment_id")int comment_id,@Param("user_id")int user_id);

    @Select("select id, user_id, comment_id, content, create_datetime " +
            "from t_learn_reply where comment_id=#{comment_id} " +
            "order by STR_TO_DATE(create_datetime, '%Y-%m-%d %H:%i:%s') desc limit #{pageSize} offset #{offset}")
    List<Reply> getReplyListByCommentId(@Param("comment_id") int comment_id,
                                        @Param("pageSize") int pageSize,
                                        @Param("offset") int offset);
    @Select("select count(id) " +
            "from t_learn_reply where comment_id=#{comment_id} ")
    Integer countReply(@Param("comment_id") int comment_id);

    @Select("select COUNT(user_id) from t_learn_reply where id=#{id}")
    Integer judgeReplyExitById(int id);

    @Select("select comment_id from t_learn_reply where id=#{id}")
    Integer getCommentIdById(int id);


    @Delete("delete from t_learn_reply where comment_id=#{comment_id}")
    Integer DeleteReplyByCommentId(int comment_id);

    @Select("select id from t_learn_reply where comment_id=#{comment_id}")
    List<Integer> getReplyIdByCommentId(int comment_id);

    @Delete("delete from t_learn_reply where id=#{id}")
    Integer DeleteReplyById(int id);

    @Select("select id, user_id, comment_id, content, create_datetime " +
            "from t_learn_reply where id=#{id}")
    Reply getReply(Integer id);



}
