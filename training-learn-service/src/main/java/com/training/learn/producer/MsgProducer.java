package com.training.learn.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.training.learn.config.RabbitMQConfig;
import com.training.learn.entity.msg.CommentMsg;
import com.training.learn.utils.JSONUtils;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class MsgProducer {
    private final RabbitTemplate rabbitTemplate;
    private final JSONUtils jsonUtils;

    /**
     * 触发生产对于用户及课程章节的校验消息
     * @param userId 用户ID
     * @param lessonId 课程ID
     * @param chapterId 章节ID
     * @return 校验消息
     * by organwalk 2023-12-25
     */
    public String triggerCommentMsg(Integer userId, Integer lessonId, Integer chapterId){
        CommentMsg msgObj = new CommentMsg(String.valueOf(UUID.randomUUID()), userId, lessonId, chapterId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COMMENT_EXCHANGE_NAME,
                "",
                jsonUtils.getJSONObjectByte((JSONObject) JSON.toJSON(msgObj)),
                message -> {
                    message.getMessageProperties().setReplyTo(RabbitMQConfig.COMMENT_RESULT_QUEUE_NAME);
                    return message;
                });

        return listenForCallback(RabbitMQConfig.COMMENT_RESULT_QUEUE_NAME, RabbitMQConfig.COMMENT_CONSUMER_NUMBER);
    }

    /**
     * 监听处理结果的回调队列并进行处理，其返回值通常是处理结果的内容
     * @param callbackQueue 处理结果的回调队列名
     * @param numberOfConsumers 该回调队列的消费者数量
     * @return 处理结果内容
     * by organwalk 2023-12-25
     */
    private String listenForCallback(String callbackQueue, Integer numberOfConsumers){
        int receivedMessagesCount = 0;
        while (receivedMessagesCount < numberOfConsumers) {
            byte[] callbackMessage = (byte[]) rabbitTemplate.receiveAndConvert(callbackQueue, 10000);
            if (Objects.isNull(callbackMessage)){
                return "内部服务错误";
            }
            String strMsg = new String(callbackMessage);
            if (!Objects.equals(strMsg, "success")){
                return strMsg;
            }
            receivedMessagesCount++;
        }
        return "success";
    }
}
