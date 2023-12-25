package com.training.learn.consumer;

import com.alibaba.fastjson.JSONObject;
import com.training.learn.config.RabbitMQConfig;
import com.training.learn.entity.msg.LikePushMsg;
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
public class LikeConsumer {
    private final LikeMapper likeMapper;
    private final LikeCache likeCache;
    private final JSONUtils jsonUtils;
    private final EventProcessMsgProducer eventProcessMsgProducer;

    enum LikeState {
        NOT_LIKED,  // 未点赞状态
        TO_LIKED,     // 从取消点赞到点赞
        LIKED, // 点赞
        NONE
    }

    @RabbitListener(queues = RabbitMQConfig.LIKE_PROCESS_QUEUE)
    public void likeProcessMessage(byte[] messageBody) {
        // 从消息体中初始化变量
        JSONObject msg = jsonUtils.getJSONObj(messageBody);
        Integer commentId = msg.getInteger("commentId");
        Integer userId = msg.getInteger("userId");
        Integer lessonId = msg.getInteger("lessonId");
        Integer commentUser = msg.getInteger("commentUser");
        Integer state = msg.getInteger("state");
        String nowDateTime = msg.getString("nowDateTime");

        //获取缓存中对应评论的点赞数
        Integer sum = (Integer) likeCache.getCommentLike(String.valueOf(lessonId), String.valueOf(commentId));

        // 获取点赞状态
        LikeState likeState = getLikeState(commentId, userId, state);
        switch (likeState) {
            case LIKED -> likeMapper.likeComment(userId, commentId, 1, nowDateTime);
            case NOT_LIKED -> likeMapper.DeleteByComIdAndUserId(commentId, userId);
            case TO_LIKED -> likeMapper.updateStateToOne(commentId, userId);
        }

        // 点赞完成时，触发生产点赞通知推送消息
        if (likeState == LikeState.LIKED || likeState == LikeState.TO_LIKED){
            eventProcessMsgProducer.triggerLikePush(
                    new LikePushMsg(String.valueOf(UUID.randomUUID()), userId, commentId, List.of(commentUser))
            );
        }

        if ((likeState == LikeState.LIKED || likeState == LikeState.NOT_LIKED || likeState == LikeState.TO_LIKED)
                && Objects.nonNull(sum)) {
            likeCache.deleteCommentLike(String.valueOf(lessonId), String.valueOf(commentId));
        }
    }

    private LikeState getLikeState(Integer commentId, Integer userId, Integer state) {
        Integer OldState = likeMapper.judgeLikeOrNot(commentId, userId);
        if (Objects.nonNull(OldState) && !Objects.equals(OldState, state)) {
            if (OldState == 0 && state == 1) {
                return LikeState.TO_LIKED;
            } else {
                return LikeState.NOT_LIKED;
            }
        } else if (Objects.isNull(OldState) && state == 1) {
            return LikeState.LIKED;
        } else {
            return LikeState.NONE;
        }
    }
}
