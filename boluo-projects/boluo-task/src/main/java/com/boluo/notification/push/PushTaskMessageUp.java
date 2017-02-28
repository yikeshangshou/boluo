package com.boluo.notification.push;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.boluo.model.Entity;
import com.boluo.task.TaskScheduler;

/**
 * @author mixueqiang
 * @since Jun 30, 2016
 */
@Service
public class PushTaskMessageUp extends AbstractPushTask {

  public PushTaskMessageUp() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 23, 30);
  }

  @Override
  public int getBusinessType() {
    return 72001;
  }

  @Override
  public String getPushContent(Entity entity) throws Exception {
    long fromUserId = entity.getLong("fromUserId");
    long toUserId = entity.getLong("toUserId");
    if (fromUserId == toUserId) {
      throw new Exception("Illegal push: fromUserId == toUserId.");
    }

    String fromUsername = entity.getString("fromUsername");
    if (StringUtils.isEmpty(fromUsername)) {
      fromUsername = "有人";
    }

    String reply = entity.getString("content");
    if (StringUtils.length(reply) > 50) {
      reply = StringUtils.substring(reply, 0, 47) + "...";
    }

    String content = entity.getString("content");
    if (StringUtils.length(content) > 20) {
      content = StringUtils.substring(content, 0, 17) + "...";
    }

    StringBuilder sb = new StringBuilder();
    sb.append(fromUsername).append("赞同了你的观点：").append(content);

    return sb.toString();
  }

  @Override
  public String getTaskTableName() {
    return "notification_message_up";
  }

}
