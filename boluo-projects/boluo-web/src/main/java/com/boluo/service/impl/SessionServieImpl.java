package com.boluo.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.boluo.Constants;
import com.boluo.dao.mapper.UserRowMapper;
import com.boluo.model.Entity;
import com.boluo.model.User;
import com.boluo.service.SessionService;

/**
 * @author mixueqiang
 * @since Jul 1, 2016
 */
@Service("sessionService")
public class SessionServieImpl extends BaseService implements SessionService {
  private static final Log LOG = LogFactory.getLog(SessionServieImpl.class);

  @Override
  public void destorySession(String sessionId) {
    if (StringUtils.isEmpty(sessionId)) {
      return;
    }

    Map<String, Object> updateValues = new HashMap<String, Object>();
    updateValues.put("status", Constants.STATUS_DISABLED); // 将session置为无效。
    updateValues.put("updateTime", System.currentTimeMillis());
    entityDao.update("user_session", "sessionId", sessionId, updateValues);
  }

  @Override
  public User signinWithCookie(String sessionId) {
    if (StringUtils.isEmpty(sessionId)) {
      return null;
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("sessionId", sessionId);
    condition.put("status", Constants.STATUS_ENABLED);
    Entity entity = entityDao.findOne("user_session", condition);

    if (entity != null) {
      long userId = entity.getLong("userId");
      User user = entityDao.get("user", userId, UserRowMapper.getInstance());

      if (user != null) {
        LOG.info("User " + user.getId() + " signs in with cookie successfully.");
        return user;
      }
    }

    return null;
  }

  @Override
  public User signinWithDeviceId(String deviceId) {
    if (StringUtils.isEmpty(deviceId)) {
      return null;
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("deviceId", deviceId);
    condition.put("phone", "");
    User user = entityDao.findOne("user", condition, UserRowMapper.getInstance());
    if (user != null) {
      // 基于设备ID默认用户。
      LOG.info("User " + user.getId() + " signs in with deviceId " + deviceId + " successfully.");
      return user;

    } else {
      // 使用设备ID注册默认用户。
      long time = System.currentTimeMillis();
      Entity entity = new Entity("user");
      entity.set("deviceId", deviceId);
      entity.set("phone", "").set("email", "").set("username", "").set("password", ""); // 邮箱、用户名默认为空。
      entity.set("locale", "cn").set("roles", "user").set("bindStatus", Constants.STATUS_NO);
      entity.set("status", Constants.STATUS_ENABLED).set("createTime", time);
      entity = entityDao.saveAndReturn(entity);

      // 记录用户新设备信息，该信息可能会重复记录。
      Entity userDevice = new Entity("user_device");
      userDevice.set("userId", entity.getId()).set("deviceId", deviceId);
      userDevice.set("status", Constants.STATUS_ENABLED).set("createTime", time);
      entityDao.save(userDevice);
      LOG.info("New device binds automatically: " + deviceId);

      user = entityDao.get("user", entity.getId(), UserRowMapper.getInstance());
      return user;
    }
  }

  @Override
  public void storeSession(long userId, String sessionId) {
    if (userId <= 0 || StringUtils.isEmpty(sessionId)) {
      return;
    }

    try {
      Map<String, Object> condition = new HashMap<String, Object>();
      condition.put("userId", userId);
      condition.put("sessionId", sessionId);
      Entity entity = entityDao.findOne("user_session", condition);

      if (entity != null) { // 已有session记录，置为可用状态。
        int status = entity.getInt("status");
        if (status != Constants.STATUS_ENABLED) {
          entityDao.update("user_session", condition, "status", Constants.STATUS_ENABLED);
        }

      } else {
        entity = new Entity("user_session");
        entity.set("userId", userId).set("sessionId", sessionId);
        entity.set("status", Constants.STATUS_ENABLED).set("createTime", System.currentTimeMillis());
        entityDao.save(entity);
        LOG.info("User " + userId + " stored session: " + sessionId);
      }

    } catch (Throwable t) {
      LOG.warn("Failed to store user session, " + userId + ":" + sessionId, t);
    }
  }

}
