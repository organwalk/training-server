package com.training.plan.consumer;

import com.alibaba.fastjson.JSONObject;
import com.training.plan.config.RabbitMQConfig;
import com.training.plan.mapper.ChapterMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class Consumer {
    private final RabbitTemplate rabbitTemplate;
    private final ChapterMapper chapterMapper;
    @RabbitListener(queues = RabbitMQConfig.COMMENT_PLAN_QUEUE)
    public void commentMessage(byte[] messageBody) {
        JSONObject msg = getJSONObj(messageBody);

        MessageProperties properties = new MessageProperties();
        properties.setExpiration("5000");

        // 发送校验结果消息到结果队列
        if (Objects.nonNull(chapterMapper.getChapterExist(msg.getInteger("chapterId"), msg.getInteger("lessonId")))) {
            Message message = new Message(getByte("success"), properties);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.COMMENT_RESULT_EXCHANGE_NAME,
                    RabbitMQConfig.COMMENT_RESULT_ROUTING_KEY,
                    message
            );
        } else {
            Message message = new Message(getByte("指定课程章节不存在"), properties);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.COMMENT_RESULT_EXCHANGE_NAME,
                    RabbitMQConfig.COMMENT_RESULT_ROUTING_KEY,
                    message
            );
        }
    }

    private JSONObject getJSONObj(byte[] messageBody){
        String jsonString = new String(messageBody);
        return JSONObject.parseObject(jsonString);
    }

    private byte[] getByte(String str){
        return str.getBytes();
    }
}
