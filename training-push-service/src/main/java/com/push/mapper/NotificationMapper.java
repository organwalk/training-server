package com.push.mapper;

import com.push.entity.table.NotificationTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {
    @Insert("insert into t_push_notification(notification_type, notification_content, notification_source_id, notification_quote_id, notification_uid, create_datetime) " +
            "values (#{obj.notificationType}, #{obj.notificationContent}, #{obj.notificationSourceId}, #{obj.notificationQuoteId}, #{obj.notificationUid}, #{obj.createDatetime})")
    @Options(useGeneratedKeys = true, keyProperty = "obj.id")
    void insertNotification(@Param("obj")NotificationTable notificationTable);

    @Select("<script>" +
            "select id, notification_type, notification_content, notification_source_id, " +
            "notification_quote_id, notification_uid, create_datetime " +
            "from t_push_notification " +
            "where id IN " +
            "<foreach item='item' collection='idList' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    List<NotificationTable> getNotifiactionList(@Param("idList") List<Integer> notificationList);
}
