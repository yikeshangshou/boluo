package com.boluo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.boluo.model.Notification;

/**
 * @author mixueqiang
 * @since Jun 29, 2016
 */
public class NotificationRowMapper implements RowMapper<Notification> {
  private static Map<String, NotificationRowMapper> instances = new HashMap<String, NotificationRowMapper>();

  public static NotificationRowMapper getInstance(String modelName) {
    if (!instances.containsKey(modelName)) {
      NotificationRowMapper instance = new NotificationRowMapper(modelName);
      instances.put(modelName, instance);
    }

    return instances.get(modelName);
  }

  private String modelName;

  private NotificationRowMapper(String modelName) {
    this.modelName = modelName;
  }

  @Override
  public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
    Notification entity = new Notification();
    entity.setId(rs.getLong("id"));
    entity.setType(rs.getInt("type"));
    entity.setFromUserId(rs.getLong("fromUserId"));
    entity.setToUserId(rs.getLong("toUserId"));
    entity.setDataId(rs.getLong("dataId"));
    entity.setContent(rs.getString("content"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    if (StringUtils.equals(Notification.MODEL_COMMENT, modelName)) {
      entity.getProperties().put("fromUsername", rs.getString("fromUsername"));
      entity.getProperties().put("feedId", rs.getLong("feedId"));
      entity.getProperties().put("feed", rs.getString("feed"));

    } else if (StringUtils.equals(Notification.MODEL_FEED, modelName)) {
      entity.getProperties().put("fromUsername", rs.getString("fromUsername"));
      entity.getProperties().put("topicId", rs.getLong("topicId"));
      entity.getProperties().put("topic", rs.getString("topic"));

    } else if (StringUtils.equals(Notification.MODEL_MESSAGE, modelName)) {
      entity.setItemId(rs.getLong("itemId"));
      entity.setDiscussionId(rs.getLong("discussionId"));
      entity.setReplyId(rs.getLong("replyId"));
      entity.setValue(rs.getLong("value"));
      entity.setLastReadTime(rs.getLong("lastReadTime"));
      entity.setLastUpTime(rs.getLong("lastUpTime"));

    } else if (StringUtils.equals(Notification.MODEL_SYSTEM, modelName)) {
      entity.setValue(rs.getLong("value"));
      entity.setLastReadTime(rs.getLong("lastReadTime"));
      entity.setLastVerifyTime(rs.getLong("lastVerifyTime"));
    }

    return entity;
  }

}
