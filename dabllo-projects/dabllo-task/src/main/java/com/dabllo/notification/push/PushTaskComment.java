package com.dabllo.notification.push;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.dabllo.model.Entity;
import com.dabllo.task.TaskScheduler;

/**
 * @author mixueqiang
 * @since Jun 30, 2016
 */
@Service
public class PushTaskComment extends AbstractPushTask {

  public PushTaskComment() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 23, 30);
  }

  @Override
  public int getBusinessType() {
    return 1000;
  }

  @Override
  public String getPushContent(Entity entity) throws Exception {
    long fromUserId = entity.getLong("fromUserId");
    long toUserId = entity.getLong("toUserId");
    if (fromUserId == toUserId) {
      throw new Exception("Illegal push: fromUserId == toUserId.");
    }

    int type = entity.getInt("type");
    String fromUsername = entity.getString("fromUsername");
    if (StringUtils.isEmpty(fromUsername)) {
      fromUsername = "有人";
    }

    String feed = entity.getString("feed");
    if (StringUtils.length(feed) > 20) {
      feed = StringUtils.substring(feed, 0, 17) + "...";
    }

    String content = entity.getString("content");
    if (StringUtils.length(content) > 20) {
      content = StringUtils.substring(content, 0, 17) + "...";
    }

    StringBuilder sb = new StringBuilder().append(fromUsername).append("在 ").append(feed);
    if (type == 1) {
      return sb.append(" 中评论了你：").append(content).toString();

    } else if (type == 2) {
      return sb.append(" 中回复了你：").append(content).toString();

    } else {
      throw new Exception("Unknown comment notification: " + entity);
    }
  }

  @Override
  public String getTaskTableName() {
    return "notification_comment";
  }

}
