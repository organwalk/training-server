package com.training.progress.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.training.progress.config.RabbitMqConfig;
import com.training.progress.entity.msg.MarkChapterMsg;
import com.training.progress.utils.JSONUtils;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventProcessMsgProducer {
    private final RabbitTemplate rabbitTemplate;
    private final JSONUtils jsonUtils;

    public void triggerMarkChapter(MarkChapterMsg msg){
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.MARK_CHAPTER_EXCHANGE,
                RabbitMqConfig.MARK_CHAPTER_ROUTING_KEY,
                jsonUtils.getJSONObjectByte((JSONObject) JSON.toJSON(msg))
        );
    }
}
