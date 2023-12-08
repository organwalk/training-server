package com.push.service;

import com.alibaba.fastjson.JSONObject;
import com.push.client.LearnClient;
import com.push.client.PlanClient;
import com.push.client.TestClient;
import com.push.client.UserClient;
import com.push.entity.PushNotification;
import com.push.entity.result.NotificationDetail;
import com.push.entity.result.NotificationTypeResult;
import com.push.entity.table.NotificationReception;
import com.push.entity.table.NotificationSource;
import com.push.entity.table.NotificationTable;
import com.push.mapper.NotificationMapper;
import com.push.mapper.NotificationReceptionMapper;
import com.push.mapper.NotificationSourceMapper;
import com.push.utils.DateUtil;
import com.training.common.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationSourceMapper notificationSourceMapper;
    private final NotificationMapper notificationMapper;
    private final NotificationReceptionMapper notificationReceptionMapper;
    private final DateUtil dateUtil;
    private final UserClient userClient;
    private final PlanClient planClient;
    private final TestClient testClient;
    private final LearnClient learnClient;

    private final static ConcurrentMap<String, Integer> sourceTypeMap = new ConcurrentHashMap<>();

    @Override
    public void notificationUser(PushNotification pushNotification, Integer uid) {

        // 如果通知来源为空，则从数据库中获取
        if (sourceTypeMap.isEmpty()) {
            List<NotificationSource> notificationSources = notificationSourceMapper.selectSourceTypeMap();
            notificationSources.forEach(item -> sourceTypeMap.put(item.getSourceType(), item.getId()));
        }

        // 插入通知消息
        NotificationTable notificationTable = new NotificationTable(
                null,
                pushNotification.getNotification_type(),
                pushNotification.getNotification_content(),
                sourceTypeMap.get(pushNotification.getNotification_source_type()),
                pushNotification.getNotification_quote_id(),
                uid,
                dateUtil.getCreateDateTime()
        );
        notificationMapper.insertNotification(notificationTable);


        // 插入消息未读
        Integer notificationId = notificationTable.getId();
        notificationReceptionMapper.batchInsertReception(
                pushNotification.getNotification_receiver_list(),
                notificationId);
    }

    @Override
    public DataRespond getNotificationList(Integer uid, Integer pageSize, Integer offset) {
        Integer sumMark = notificationReceptionMapper.countReceptionList(uid);
        if (sumMark == 0) {
            return new DataPagingSuccessRespond("您还没有收到任何通知", 0, new ArrayList<>());
        }
        List<NotificationDetail> processNotificationList = new ArrayList<>();
        // 获取接收者列表
        List<NotificationReception> notificationReceptions = notificationReceptionMapper.selectReceptionList(uid, pageSize, offset);

        // 获取接收者通知详情列表
        List<NotificationTable> notifiactionList = getNotificationTableList(notificationReceptions);

        // 反转通知源映射列表
        ConcurrentMap<Integer, String> reversedMap = reversedSourceMap();

        // 处理原始列表的通知发出者
        notifiactionList.forEach(item -> {
            // 获取阅读状态

            Integer isRead = getReadState(notificationReceptions, item.getId());
            // 获取发起人信息
            String sender = getSender(item.getNotificationUid());
            // 获取指定通知源的引用内容
            Object context = getQuote(reversedMap.get(item.getNotificationSourceId()), item.getNotificationQuoteId());
            // 添加处理后的通知细节
            processNotificationList.add(getNotificationDetail(item, reversedMap.get(item.getNotificationSourceId()), context, sender, isRead));
        });

        return new DataPagingSuccessRespond("已成功获取通知列表", sumMark, processNotificationList);
    }

    @Override
    public MsgRespond readNotification(Integer uid, Integer notificationId) {
        notificationReceptionMapper.updateNotificationRead(uid, notificationId);
        return MsgRespond.success("已成功将此通知标记为已读");
    }

    @Override
    public DataRespond getTypeNotificationList(Integer uid, Integer sourceId, Integer pageSize, Integer offset) {
        Integer sumMark = notificationReceptionMapper.countNotificationTypeResult(uid, sourceId);
        if (sumMark == 0) {
            return new DataFailRespond("该类别下不存在通知");
        }

        // 反转通知源映射列表
        ConcurrentMap<Integer, String> reversedMap = reversedSourceMap();

        List<NotificationTypeResult> typeNotificationList = notificationReceptionMapper.selectNotificationTypeResult(uid, sourceId, pageSize, offset);
        List<NotificationDetail> processNotificationList = new ArrayList<>();
        // 处理原始列表的通知发出者
        typeNotificationList.forEach(item -> {
            String sender = getSender(item.getNotificationUid());
            // 获取指定通知源的引用内容
            Object context = getQuote(reversedMap.get(item.getNotificationSourceId()), item.getNotificationQuoteId());
            // 添加处理后的通知细节
            processNotificationList.add(getTypeNotificationDetail(item, reversedMap.get(item.getNotificationSourceId()), context, sender));
        });

        return new DataPagingSuccessRespond("已成功获取通知列表", sumMark, processNotificationList);
    }

    private ConcurrentMap<Integer, String> reversedSourceMap(){
        // 反转通知源映射列表
        if (sourceTypeMap.isEmpty()) {
            List<NotificationSource> notificationSources = notificationSourceMapper.selectSourceTypeMap();
            notificationSources.forEach(item -> sourceTypeMap.put(item.getSourceType(), item.getId()));
        }
        return sourceTypeMap.entrySet().stream()
                .collect(Collectors.toConcurrentMap(
                        Map.Entry::getValue,
                        Map.Entry::getKey
                ));
    }


    private List<NotificationTable> getNotificationTableList(List<NotificationReception> notificationReceptions) {
        List<Integer> notificationIdList = notificationReceptions.stream()
                .map(NotificationReception::getNotificationId)
                .toList();
        return notificationMapper.getNotifiactionList(notificationIdList);
    }


    private Integer getReadState(List<NotificationReception> notificationReceptions, Integer id) {
        return notificationReceptions.stream()
                .filter(obj -> Objects.equals(obj.getNotificationId(), id))
                .map(NotificationReception::getIsRead)
                .findFirst()
                .orElse(0);
    }

    private String getSender(Integer notificationUid) {
        JSONObject userInfo = userClient.getUserAccountByUid(notificationUid).join();
        return userInfo.getJSONObject("data").getString("realName");
    }

    private Object getQuote(String sourceType, Integer quoteId) {
        switch (sourceType) {
            case "plan" -> {
                return getPlanQuote(quoteId);
            }
            case "test" -> {
                return getTestQuote(quoteId);
            }
            case "father_like" -> {
                return getFatherLikeQuote(quoteId);
            }
            case "children_like", "reply" -> {
                return getReplyQuote(quoteId);
            }
            default -> {
                return null;
            }
        }
    }

    private Object getPlanQuote(Integer quoteId) {
        JSONObject planInfo = planClient.getPlanInfoById(quoteId).join();
        @Data
        @AllArgsConstructor
        class planQuote {
            private String training_title;
        }
        return new planQuote(planInfo.getJSONObject("data").getJSONObject("table").getString("training_title"));
    }

    private Object getTestQuote(Integer quoteId) {
        JSONObject testInfo = testClient.getTestInfo(quoteId).join();
        @Data
        @AllArgsConstructor
        class testQuote {
            private String test_title;
            private String start_datetime;
            private String end_datetime;
        }
        return new testQuote(
                testInfo.getJSONObject("data").getString("test_title"),
                testInfo.getJSONObject("data").getString("start_datetime"),
                testInfo.getJSONObject("data").getString("end_datetime")
        );
    }

    private Object getFatherLikeQuote(Integer quoteId) {
        JSONObject fatherComment = learnClient.getFatherComment(quoteId).join();
        @Data
        @AllArgsConstructor
        class fatherLikeQuote {
            private String content;
        }
        return new fatherLikeQuote(fatherComment.getJSONObject("data").getString("content"));
    }

    private Object getReplyQuote(Integer quoteId) {
        JSONObject childrenComment = learnClient.getChildrenComment(quoteId).join();
        @Data
        @AllArgsConstructor
        class childrenLikeQuote {
            private String content;
        }
        return new childrenLikeQuote(childrenComment.getJSONObject("data").getString("content"));
    }

    private NotificationDetail getNotificationDetail(NotificationTable item, String sourceType, Object context, String sender, Integer isRead) {
        return NotificationDetail.builder()
                .id(item.getId())
                .notification_type(item.getNotificationType())
                .notification_content(item.getNotificationContent())
                .notification_source_type(sourceType)
                .notification_quote(context)
                .notification_sender(sender)
                .create_datetime(item.getCreateDatetime())
                .is_read(isRead)
                .build();
    }

    private NotificationDetail getTypeNotificationDetail(NotificationTypeResult item, String sourceType, Object context, String sender){
        return NotificationDetail.builder()
                .notification_type(item.getNotificationType())
                .notification_content(item.getNotificationContent())
                .notification_source_type(sourceType)
                .notification_quote(context)
                .notification_sender(sender)
                .create_datetime(item.getCreateDatetime())
                .is_read(item.getIsRead())
                .build();
    }
}
