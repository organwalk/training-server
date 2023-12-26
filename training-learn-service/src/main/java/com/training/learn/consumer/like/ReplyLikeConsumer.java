package com.training.learn.consumer.like;

import com.alibaba.fastjson.JSONObject;
import com.training.learn.config.RabbitMQConfig;
import com.training.learn.entity.msg.ReplyLikePushMsg;
import com.training.learn.mapper.CommentMapper;
import com.training.learn.mapper.LikeMapper;
import com.training.learn.producer.EventProcessMsgProducer;
import com.training.learn.reposoty.LikeCache;
import com.training.learn.utils.JSONUtils;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ReplyLikeConsumer {
    private final JSONUtils jsonUtils;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final LikeCache likeCache;
    private final LikeState likeState;
    private final EventProcessMsgProducer eventProcessMsgProducer;

    @RabbitListener(queues = RabbitMQConfig.REPLY_LIKE_PROCESS_QUEUE)
    public void replyLikeProcessMessage(byte[] messageBody){
        // 从消息体中初始化变量
        JSONObject msg = jsonUtils.getJSONObj(messageBody);
        Integer userId = msg.getInteger("userId");
        Integer replyId = msg.getInteger("replyId");
        Integer commentId = msg.getInteger("commentId");
        Integer replyUser = msg.getInteger("replyUser");
        Integer state = msg.getInteger("state");
        String nowDateTime = msg.getString("nowDateTime");

        //通过评论Id获取课程id
        Integer lessonId;
        String cache = likeCache.getCommentLessonIdCache(commentId);
        if (cache.isBlank()) {
            lessonId = commentMapper.getLessonIdByCommentId(commentId);
        }else {
            lessonId = Integer.valueOf(cache.split("-")[0]);
        }

        //获取缓存中的点赞数
        Integer sum = (Integer) likeCache.getReplyLike(String.valueOf(lessonId), String.valueOf(replyId));

        Integer oldState = likeMapper.getStateByReplyId(replyId, userId);
        StateEnum nowState = likeState.getLikeState(oldState, state);
        switch (nowState){
            case NOT_LIKED -> likeMapper.UpdateReplyStateSetZero(replyId, userId);
            case LIKED -> likeMapper.LikeReply(userId, replyId, 1, nowDateTime);
            case TO_LIKED -> likeMapper.UpdateReplyStateSetOne(replyId, userId);
        }

        // 点赞完成时，触发生产点赞通知推送消息
        if (nowState == StateEnum.LIKED || nowState == StateEnum.TO_LIKED){
            eventProcessMsgProducer.triggerReplyLikePush(
                    new ReplyLikePushMsg(String.valueOf(UUID.randomUUID()), userId, replyId, List.of(replyUser))
            );
        }

        if ((nowState == StateEnum.LIKED || nowState == StateEnum.TO_LIKED || nowState == StateEnum.NOT_LIKED)
                && Objects.nonNull(sum)){
            likeCache.deleteReplyLike(String.valueOf(lessonId), String.valueOf(replyId));
        }
    }


}
