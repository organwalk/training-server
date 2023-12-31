package com.push.mapper;

import com.push.entity.table.NotificationTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * t_push_notification表mapper接口
 * by organwalk 2023-11-29
 */
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
            "order by FIELD(id, " +
            "<foreach item='item' collection='idList' separator=','>" +
            "#{item}" +
            "</foreach>" +
            ")</script>")
    List<NotificationTable> getNotifiactionList(@Param("idList") List<Integer> notificationList);

    @Delete("delete from t_push_notification where id = #{id}")
    void deleteNotification(Integer id);
}
