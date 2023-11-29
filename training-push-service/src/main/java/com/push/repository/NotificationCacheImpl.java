package com.push.repository;

import com.alibaba.fastjson.JSON;
import com.push.entity.result.NotificationDetail;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class NotificationCacheImpl implements NotificationCache{
    private final RedisTemplate<String, Object> redisTemplate;

    private String getStrNotificationKey(Integer uid){
        return "notification-" + uid;
    }

    @Override
    public void saveNotification(Integer uid, List<NotificationDetail> list) {
        redisTemplate.opsForValue().set(getStrNotificationKey(uid), JSON.toJSONString(list));
        redisTemplate.expire(getStrNotificationKey(uid), 3, TimeUnit.DAYS);
    }

    @Override
    public List<NotificationDetail> getNotification(Integer uid) {
        Object jsonString = redisTemplate.opsForValue().get(getStrNotificationKey(uid));
        return Objects.nonNull(jsonString) ? JSON.parseArray(String.valueOf(jsonString), NotificationDetail.class) : new ArrayList<>();
    }

    @Override
    public void deleteNotification(Integer uid) {
        redisTemplate.delete(getStrNotificationKey(uid));
    }
}
