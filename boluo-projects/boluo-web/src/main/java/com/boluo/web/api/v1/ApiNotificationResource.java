package com.boluo.web.api.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.boluo.Constants;
import com.boluo.dao.BaseDao;
import com.boluo.dao.mapper.DiscussionRowMapper;
import com.boluo.dao.mapper.ItemRowMapper;
import com.boluo.dao.mapper.NotificationRowMapper;
import com.boluo.dao.mapper.TopicRowMapper;
import com.boluo.dao.mapper.UserRowMapper;
import com.boluo.model.Discussion;
import com.boluo.model.Entity;
import com.boluo.model.Item;
import com.boluo.model.Notification;
import com.boluo.model.Topic;
import com.boluo.model.User;
import com.boluo.util.PageNumberUtils;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Jun 29, 2016
 */
@Path("/api/v1/notification")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiNotificationResource extends BaseResource {

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getNotification() {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Map<String, Integer> result = new HashMap<String, Integer>();
    result.put("drawerAlert", 0);

    result.put("messageAlert", 0);
    result.put("message", 0);
    result.put("system", 0);
    // TODO user过几个版本就可以删掉了
    result.put("user", 0);

    result.put("userFollow", 0);
    result.put("userDiscussion", 0);
    result.put("userTopic", 0);

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("toUserId", user.getId());
    condition.put("status", Constants.STATUS_ENABLED);
    List<Notification> notifications = entityDao.find("notification_message", condition, 1, PageNumberUtils.PAGE_SIZE_XSMALL, NotificationRowMapper.getInstance(Notification.MODEL_MESSAGE),
        "lastUpTime", BaseDao.ORDER_OPTION_DESC);
    List<Notification> systemNotifications = entityDao.findByOffset("notification_system", condition, 1, PageNumberUtils.PAGE_SIZE_SMALL, NotificationRowMapper.getInstance(Notification.MODEL_SYSTEM));
    if (CollectionUtils.isNotEmpty(notifications)) {
      for (Notification notification : notifications) {
        if (notification.getLastUpTime() > notification.getLastReadTime()) {
          result.put("message", 1);
          break;
        }
      }
    }
    if (CollectionUtils.isNotEmpty(systemNotifications)) {
      for (Notification notification : systemNotifications) {
        if (notification.getLastVerifyTime() != 0 && notification.getLastVerifyTime() > notification.getLastReadTime()) {
          result.put("system", 1);
          break;
        }
      }
    }

    long readAllTime = 0L;
    Entity operation = entityDao.findOne("user_operation", "userId", user.getId());
    if (operation != null) {
      readAllTime = operation.getLong("readAllTime");
    }
    condition.remove("toUserId");
    condition.put("userId", user.getId());
    List<Entity> entities = entityDao.find("discussion_follow", condition, 1, PageNumberUtils.PAGE_SIZE_XXLARGE);
    if (CollectionUtils.isNotEmpty(entities)) {
      for (Entity entity : entities) {
        Discussion discussion = entityDao.get("discussion", entity.getLong("discussionId"), DiscussionRowMapper.getInstance());
        if (discussion.getStatus() != 1) {
			continue;
		}
        if (discussion.getLastReplyTime() > readAllTime && discussion.getLastReplyTime() > entity.getLong("lastReadTime")) {
          result.put("user", 1);
          result.put("userDiscussion", 1);
          break;
        }
      }
    }

    Map<String, Object> topicCondition = new HashMap<String, Object>();
    topicCondition.put("userId", user.getId());
    topicCondition.put("status", Constants.STATUS_ENABLED);
    List<Entity> topicNotifications = entityDao.find("topic_follow", topicCondition, 1, PageNumberUtils.PAGE_SIZE_XXLARGE);
    if (CollectionUtils.isNotEmpty(topicNotifications)) {
      for (Entity notification : topicNotifications) {
        Topic topic = entityDao.get("topic", notification.getLong("topicId"), TopicRowMapper.getInstance());
        if (topic == null || topic.getStatus() != 1)
          continue;
        if (topic.getLastUpdateTime() > notification.getLong("lastReadTime")) {
          result.put("userTopic", 1);
          break;
        }
      }
    }

    // TODO:检查关注的专题是否需要红点

    if (result.get("message") == 1 || result.get("system") == 1) {
      result.put("messageAlert", 1);
    }
    if (result.get("userDiscussion") == 1 || result.get("userTopic") == 1) {
      result.put("userFollow", 1);
    }
    if (result.get("messageAlert") == 1 || result.get("userFollow") == 1) {
      result.put("drawerAlert", 1);
    }
    return ResponseBuilder.ok(result);
  }

  @GET
  @Path("comment")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getCommentNotifications(@QueryParam("offset") @DefaultValue("0") long offset) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("toUserId", user.getId());
    List<Notification> notifications = entityDao.findByOffset("notification_comment", condition, offset, 15, NotificationRowMapper.getInstance(Notification.MODEL_COMMENT));
    if (CollectionUtils.isEmpty(notifications)) {
      // 没有查询到数据，直接返回。
      return ResponseBuilder.ok(null, -1);
    }

    for (Notification notification : notifications) {
      User fromUser = entityDao.get("user", notification.getFromUserId(), UserRowMapper.getInstance());
      notification.getProperties().put("fromUsername", fromUser.getUsername());
      notification.getProperties().put("fromUserAvatar", fromUser.getAvatar());

      String feed = (String) notification.getProperties().get("feed");
      if (StringUtils.length(feed) > 20) {
        feed = StringUtils.substring(feed, 0, 17) + "...";
        notification.getProperties().put("feed", feed);
      }

      String comment = notification.getContent();
      if (StringUtils.length(comment) > 20) {
        comment = StringUtils.substring(comment, 0, 17) + "...";
        notification.setContent(comment);
      }

      long id = notification.getId();
      offset = offset > id ? id : offset;
    }

    return ResponseBuilder.ok(notifications, offset);
  }

  @GET
  @Path("message")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getMessageNotifications(@QueryParam("offset") @DefaultValue("0") long offset) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("toUserId", user.getId());
    // condition.put("status", Constants.STATUS_ENABLED);
    List<Notification> notifications = entityDao.findByOffset("notification_message", condition, offset, PageNumberUtils.PAGE_SIZE_SMALL,
        NotificationRowMapper.getInstance(Notification.MODEL_MESSAGE));
    if (CollectionUtils.isEmpty(notifications)) {
      // 没有查询到数据，直接返回。
      return ResponseBuilder.ok(null, -1);
    }

    long time = System.currentTimeMillis();
    for (Notification notification : notifications) {
      notification.getProperties().put("title", "收到了" + notification.getValue() + "个赞同");
      if (notification.getUpdateTime() == 0) {
        notification.setUpdateTime(notification.getCreateTime());
      }

      Map<String, Object> updateValues = new HashMap<String, Object>();
      updateValues.put("lastReadTime", time);
      entityDao.update("notification_message", "id", notification.getId(), updateValues);

      long id = notification.getId();
      offset = offset > id ? id : offset;
    }

    return ResponseBuilder.ok(notifications, offset);
  }

  @GET
  @Path("system")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getSystemNotifications(@QueryParam("offset") @DefaultValue("0") long offset) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("toUserId", user.getId());
    List<Notification> notifications = entityDao.findByOffset("notification_system", condition, offset, PageNumberUtils.PAGE_SIZE_SMALL, NotificationRowMapper.getInstance(Notification.MODEL_SYSTEM));
    if (CollectionUtils.isEmpty(notifications)) {
      // 没有查询到数据，直接返回。
      return ResponseBuilder.ok(null, -1);
    }

    for (Notification notification : notifications) {
      fillNotificationContent(notification);

      Map<String, Object> updateValues = new HashMap<String, Object>();
      updateValues.put("lastReadTime", System.currentTimeMillis());
      entityDao.update("notification_system", "id", notification.getId(), updateValues);

      long id = notification.getId();
      offset = offset > id ? id : offset;
    }

    return ResponseBuilder.ok(notifications, offset);
  }

  private void fillNotificationContent(Notification notification) {
    int type = notification.getType();
    if (type == 1000 || type == 1001) {
      long feedId = notification.getDataId();
      Entity entity = entityDao.findOne("feed_to_item_review", "feedId", feedId);
      int value = entity.getInt("value");
      notification.getProperties().put("review_result", value);

      if (value == 1) {
        Item item = entityDao.get("item", entity.getLong("itemId"), ItemRowMapper.getInstance());
        notification.getProperties().put("item", item);
      }

    } else {

    }
  }

}
