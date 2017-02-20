package com.dabllo.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.dabllo.Constants;
import com.dabllo.dao.EntityDao;
import com.dabllo.model.Entity;
import com.dabllo.task.TaskScheduler;

/**
 * @author mixueqiang
 * @since Aug 6, 2016
 */
@Service
public class MessageNotificationBuildingTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(MessageNotificationBuildingTask.class);

  @Resource
  private EntityDao entityDao;

  public MessageNotificationBuildingTask() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 47, 23);
  }

  public MessageNotificationBuildingTask(EntityDao entityDao) {
    this.entityDao = entityDao;
  }

  @Override
  public void run() {
    try {
      Map<String, Object> condition = new HashMap<String, Object>();
      condition.put("type", 72001);
      condition.put("status", Constants.STATUS_DISABLED);
      List<Entity> entities = entityDao.find("notification_message", condition, 1, 10);
      if (CollectionUtils.isEmpty(entities)) {
        return;
      }

      LOG.info("Get " + entities.size() + " notification_message records to update.");

      condition = new HashMap<String, Object>();
      condition.put("value", 1);
      condition.put("status", Constants.STATUS_ENABLED);
      for (Entity entity : entities) {
        long replyId = entity.getLong("dataId");
        condition.put("replyId", replyId);

        try {
          int count = entityDao.count("reply_up", condition);
          if (count > 0) {
            Map<String, Object> updateValues = new HashMap<String, Object>();
            updateValues.put("value", count);
            updateValues.put("status", Constants.STATUS_ENABLED);
            updateValues.put("updateTime", System.currentTimeMillis());
            entityDao.update("notification_message", "id", entity.getId(), updateValues);

            LOG.info("Reply " + replyId + " has " + count + " ups.");
          }

        } catch (Throwable t) {
          LOG.error("Error occurs on processing message notification building task: " + replyId, t);
        }
      }

    } catch (Throwable t) {
      LOG.error("Error occurs on processing message notification building task!", t);
    }
  }

}
