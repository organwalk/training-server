package com.training.learn.mapper;


import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeMapper {
    @Insert("insert into t_learn_like(user_id, comment_id, state, create_datetime) VALUES(#{user_id},#{comment_id},#{state},#{create_datetime}) ")
    Integer likeComment(@Param("user_id")int user_id,@Param("comment_id")int comment_id,@Param("state")int state,@Param("create_datetime")String create_datetime);

    @Select("select state from t_learn_like where comment_id=#{comment_id} and user_id=#{user_id}")
    Integer judgeLikeOrNot(@Param("comment_id")int comment_id,@Param("user_id")int user_id);

    @Update("update t_learn_like set state=0 where comment_id=#{comment_id} and user_id=#{user_id}")
    Integer updateStateToZero(@Param("comment_id")int comment_id,@Param("user_id")int user_id);

    @Update(("update t_learn_like set state=1 where comment_id=#{comment_id} and user_id=#{user_id}"))
    Integer updateStateToOne(@Param("comment_id")int comment_id,@Param("user_id")int user_id);

    @Select("select COUNT(id) from t_learn_like where comment_id=#{comment_id} and reply_id IS NULL and state=1")
    Integer getCommentLikeCount(int comment_id);

    @Select("select COUNT(id) from t_learn_like where  reply_id=#{reply_id} and state=1")
    Integer getReplyLikeCountByComIDAndReplyId(@Param("reply_id")int reply_id);

    @Select("select state from t_learn_like where comment_id=#{comment_id} and reply_id IS NULL")
    Integer getStateByCommentId(int comment_id);

    @Select("select  state from t_learn_like where comment_id=#{comment_id} and reply_id=#{reply_id} and user_id =#{user_id}")
    Integer getStateByCommentIdAndReplyId(@Param("comment_id")int comment_id,@Param("reply_id")int reply_id,@Param("user_id")int user_id);

    @Select("select state from t_learn_like where reply_id=#{reply_id} and user_id =#{user_id}")
    Integer getStateByReplyId(@Param("reply_id")int reply_id,@Param("user_id")int user_id);

    @Select("select state from t_learn_like where user_id=#{user_id} and reply_id=#{reply_id}")
    Integer judgeReplyLikeOrNot(@Param("user_id")int user_id,@Param("reply_id")int reply_id);

    @Update("update t_learn_like set state=1 where reply_id=#{reply_id} and user_id=#{user_id}")
    Integer UpdateReplyStateSetOne(@Param("reply_id")int reply_id,@Param("user_id")int user_id);

    @Update("update t_learn_like set state=0 where reply_id=#{reply_id} and user_id=#{user_id}")
    Integer UpdateReplyStateSetZero(@Param("reply_id")int reply_id,@Param("user_id")int user_id);

    @Insert("insert into t_learn_like(user_id, reply_id, state, create_datetime)values(#{user_id},#{reply_id},#{state},#{create_datetime}) ")
    Integer LikeReply(@Param("user_id")int user_id,@Param("reply_id")int reply_id,@Param("state")int state,@Param("create_datetime")String create_datetime);

    @Delete("delete from t_learn_like where comment_id=#{comment_id} and user_id=#{user_id}")
    void DeleteByComIdAndUserId(@Param("comment_id")int comment_id,@Param("user_id")int user_id);
}
