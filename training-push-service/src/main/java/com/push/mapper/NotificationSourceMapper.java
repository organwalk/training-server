package com.push.mapper;

import com.push.entity.table.NotificationSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * t_push_notification_source表mapper接口
 * by organwalk 2023-11-29
 */
@Mapper
public interface NotificationSourceMapper {
    @Select("select id, source_type from t_push_notification_source")
    List<NotificationSource> selectSourceTypeMap();
}
