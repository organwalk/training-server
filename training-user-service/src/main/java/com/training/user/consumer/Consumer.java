package com.training.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.training.user.config.RabbitMQConfig;
import com.training.user.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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
    private final UserMapper userMapper;
    @SneakyThrows
    @RabbitListener(queues = RabbitMQConfig.COMMENT_USER_QUEUE)
    public void commentMessage(byte[] messageBody) {
        JSONObject msg = getJSONObj(messageBody);

        MessageProperties properties = new MessageProperties();
        properties.setExpiration("5000");

        if (Objects.nonNull(userMapper.selectUserInfoByUid(msg.getInteger("userId")))) {
            Message message = new Message(getByte("success"), properties);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.COMMENT_RESULT_EXCHANGE_NAME,
                    RabbitMQConfig.COMMENT_RESULT_ROUTING_KEY,
                    message
            );
        } else {
            Message message = new Message(getByte("指定用户不存在"), properties);
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
