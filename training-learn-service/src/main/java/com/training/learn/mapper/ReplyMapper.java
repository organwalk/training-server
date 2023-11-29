package com.training.learn.mapper;

import com.training.learn.entity.table.Reply;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReplyMapper {
    @Insert("insert into t_learn_reply(user_id, comment_id, content, create_datetime) VALUES (#{user_id},#{comment_id},#{content},#{create_datetime})")
    Integer replyComment(@Param("user_id")int user_id,@Param("comment_id")int comment_id,@Param("content")String content,@Param("create_datetime")String create_datetime);

    @Select("select COUNT(id) from t_learn_reply where comment_id=#{comment_id} and user_id=#{user_id}")
    Integer judgeReplyExit(@Param("comment_id")int comment_id,@Param("user_id")int user_id);

    @Select("select * from t_learn_reply where comment_id=#{comment_id}")
    List<Reply> getReplyListByCommentId(int comment_id);

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
