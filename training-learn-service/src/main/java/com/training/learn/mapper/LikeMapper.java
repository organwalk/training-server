package com.training.learn.mapper;


import org.apache.ibatis.annotations.*;

/**
 * by zhaozhifeng 2023-11-10
 * by linguowei 2023-11-20
 */
@Mapper
public interface LikeMapper {
    @Insert("insert into t_learn_like(user_id, comment_id, state, create_datetime) VALUES(#{user_id},#{comment_id},#{state},#{create_datetime}) ")
    void likeComment(@Param("user_id")int user_id,@Param("comment_id")int comment_id,@Param("state")int state,@Param("create_datetime")String create_datetime);

    @Select("select state from t_learn_like where comment_id=#{comment_id} and user_id=#{user_id}")
    Integer judgeLikeOrNot(@Param("comment_id")int comment_id,@Param("user_id")int user_id);

    @Update(("update t_learn_like set state=1 where comment_id=#{comment_id} and user_id=#{user_id}"))
    void updateStateToOne(@Param("comment_id")int comment_id,@Param("user_id")int user_id);

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

    @Update("update t_learn_like set state=1 where reply_id=#{reply_id} and user_id=#{user_id}")
    void UpdateReplyStateSetOne(@Param("reply_id")int reply_id,@Param("user_id")int user_id);

    @Update("update t_learn_like set state=0 where reply_id=#{reply_id} and user_id=#{user_id}")
    void UpdateReplyStateSetZero(@Param("reply_id")int reply_id,@Param("user_id")int user_id);

    @Insert("insert into t_learn_like(user_id, reply_id, state, create_datetime)values(#{user_id},#{reply_id},#{state},#{create_datetime}) ")
    void LikeReply(@Param("user_id")int user_id,@Param("reply_id")int reply_id,@Param("state")int state,@Param("create_datetime")String create_datetime);

    @Delete("delete from t_learn_like where comment_id=#{comment_id} and user_id=#{user_id}")
    void DeleteByComIdAndUserId(@Param("comment_id")int comment_id,@Param("user_id")int user_id);
}
