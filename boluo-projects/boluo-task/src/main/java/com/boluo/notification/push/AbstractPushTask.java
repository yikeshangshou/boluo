package com.boluo.notification.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.boluo.dao.EntityDao;
import com.boluo.model.Entity;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

/**
 * @author mixueqiang
 * @since Jun 30, 2016
 */
public abstract class AbstractPushTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(AbstractPushTask.class);
  private static final String APP_KEY = "b5ca6cf2ee893f83a522f807";
  private static final String MASTER_SECRET = "c37e6bcf18aa08c4dd05b07f";
  protected static final JPushClient JPUSH_CLIENT = new JPushClient(MASTER_SECRET, APP_KEY);

  @Resource
  protected EntityDao entityDao;

  /**
   * Get business type.
   */
  public abstract int getBusinessType();

  protected long getDataId(Entity entity) {
    return entity.getLong("dataId");
  }

  /**
   * Build push content.
   */
  public abstract String getPushContent(Entity entity) throws Exception;

  protected String getPushType() {
    return null;
  }

  /**
   * Get push registration Id.
   */
  protected List<String> getRegistrationIds(Entity entity) throws Exception {
    long userId = entity.getLong("toUserId");
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("userId", userId);
    condition.put("status", 1);
    Entity userDevice = entityDao.findOne("user_device", condition);

    if (userDevice != null) {
      List<String> list = new ArrayList<String>();
      list.add(userDevice.getString("registrationId"));
      return list;
    }

    throw new Exception("User " + userId + " has no registration id.");
  }

  /**
   * Get task table name.
   */
  public abstract String getTaskTableName();

  @Override
  public void run() {
    String tableName = getTaskTableName();
    try {
      Map<String, Object> condition = new HashMap<String, Object>();
      condition.put("status", 0);
      if (StringUtils.isNotEmpty(getPushType())) {
        condition.put("type", getPushType());
      }

      List<Entity> entities = entityDao.find(tableName, condition, 1, 10);
      if (CollectionUtils.isEmpty(entities)) {
        return;
      }
      LOG.info("Get " + entities.size() + " " + getBusinessType() + " notifications.");

      for (Entity entity : entities) {
        Map<String, Object> values = new HashMap<String, Object>();
        try {
          long dataId = getDataId(entity);
          List<String> registrationIds = getRegistrationIds(entity);
          String content = getPushContent(entity);
          PushResult result = sendPush(registrationIds, content, getBusinessType(), dataId);
          if (registrationIds != null) {
            LOG.info("Send push " + entity.getId() + " to: " + registrationIds);
          } else {
            LOG.info("Send push " + entity.getId() + " to: " + "null/all");
          }

          if (result != null && result.isResultOK()) {
            values.put("status", 1);

          } else {
            values.put("status", -1);
            LOG.error("push fail! registrationIds: " + registrationIds + "content: " + content);
          }

        } catch (Throwable t) {
          LOG.error("Error occurs on process notification task!", t);
          values.put("status", -1);
        }

        values.put("updateTime", System.currentTimeMillis());
        entityDao.update(tableName, "id", entity.getId(), values);
      }

    } catch (Throwable t) {
      LOG.error("Error occurs on processing notification task!", t);
    }
  }

  protected PushResult sendPush(List<String> registrationId, String content, int type, long dataId) throws Exception {
    return sendPush(registrationId, content, type, dataId, true);
  }

  protected PushResult sendPush(List<String> registrationId, String content, int type, long dataId, Boolean apnsProduction) throws Exception {
    // Send push.
    if (registrationId == null || registrationId.size() == 0) {
      return null;
    }
    PushPayload payload = PushPayload.newBuilder() //
        .setPlatform(Platform.ios()) //
        .setAudience(Audience.registrationId(registrationId)) //
        .setNotification(Notification.newBuilder()
            .addPlatformNotification(IosNotification.newBuilder() //
                .setAlert(content) //
                .setBadge(1).setSound("happy.caf") //
                .addExtra("jumpType", type).addExtra("jumpObjectId", dataId).build())
            .build()) //
        .setMessage(Message.content(content)) //
        .setOptions(Options.newBuilder().setApnsProduction(apnsProduction).build()) //
        .build();

    PushResult result = JPUSH_CLIENT.sendPush(payload);
    return result;
  }

}
