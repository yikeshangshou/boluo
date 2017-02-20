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
public class PushTaskSystem extends AbstractPushTask {

  public PushTaskSystem() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 23, 30);
  }

  @Override
  public int getBusinessType() {
    return 1001;
  }

  @Override
  public String getPushContent(Entity entity) throws Exception {
    String content = entity.getString("content");

    if (StringUtils.isNotEmpty(content)) {
      return content;

    } else {
      throw new Exception("Null content notification fount: " + entity);
    }
  }

  @Override
  public String getTaskTableName() {
    return "notification_system";
  }

}
