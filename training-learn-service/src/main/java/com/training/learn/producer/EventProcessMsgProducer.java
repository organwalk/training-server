package com.training.learn.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.training.learn.config.RabbitMQConfig;
import com.training.learn.entity.msg.LikeMsg;
import com.training.learn.entity.msg.LikePushMsg;
import com.training.learn.entity.msg.TestMsg;
import com.training.learn.utils.JSONUtils;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventProcessMsgProducer {
    private final RabbitTemplate rabbitTemplate;
    private final JSONUtils jsonUtils;

    /**
     * 触发试卷提交处理，生产处理消息
     * @param testMsg 试卷处理消息
     * by organwalk 2023-12-25
     */
    public void triggerTestProcess(TestMsg testMsg){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TEST_PROCESS_EXCHANGE,
                RabbitMQConfig.TEST_PROCESS_ROUTING_KEY,
                jsonUtils.getJSONObjectByte((JSONObject) JSON.toJSON(testMsg))
        );
    }

    /**
     * 触发点赞处理，生产处理消息
     * @param likeMsg 点赞处理消息
     */
    public void triggerLikeProcess(LikeMsg likeMsg){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.LIKE_PROCESS_EXCHANGE,
                RabbitMQConfig.LIKE_PROCESS_ROUTING_KEY,
                jsonUtils.getJSONObjectByte((JSONObject) JSON.toJSON(likeMsg))
        );
    }

    public void triggerLikePush(LikePushMsg likePushMsg){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.LIKE_PUSH_EXCHANGE,
                RabbitMQConfig.LIKE_PUSH_ROUTING_KEY,
                jsonUtils.getJSONObjectByte((JSONObject) JSON.toJSON(likePushMsg))
        );
    }
}
