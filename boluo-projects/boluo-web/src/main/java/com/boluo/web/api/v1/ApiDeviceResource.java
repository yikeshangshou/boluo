package com.boluo.web.api.v1;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.boluo.Constants;
import com.boluo.model.Entity;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Jun 27, 2016
 */
@Path("/api/v1/device")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiDeviceResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiDeviceResource.class);

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@FormParam("appVersion") String appVersion, @FormParam("deviceId") String deviceId, @FormParam("registrationId") String registrationId,
      @FormParam("phone") String phone, @FormParam("osVersion") String osVersion, @FormParam("network") String network) {
    long userId = getSessionUserId();
    LOG.info("UserDevice: " + userId + "@" + deviceId + ": " + registrationId);

    if (StringUtils.isEmpty(registrationId)) {
      return ResponseBuilder.error(50000, "参数 registrationId 不可以为空。");
    }

    try {
      long time = System.currentTimeMillis();

      Map<String, Object> condition = new HashMap<String, Object>();
      condition.put("userId", userId);
      condition.put("deviceId", deviceId);
      condition.put("registrationId", registrationId);
      condition.put("status", Constants.STATUS_ENABLED);
      if (entityDao.exists("user_device", condition)) {
        Map<String, Object> updateValues = new HashMap<String, Object>();
        updateValues.put("appVersion", appVersion);
        updateValues.put("phone", phone);
        updateValues.put("osVersion", osVersion);
        updateValues.put("network", network);
        entityDao.update("user_device", condition, updateValues);
        return ResponseBuilder.OK;

      } else {
        // 如果有其他人有相同的 registrationId，设置状态为不可用。
        condition.remove("userId");
        condition.remove("deviceId");
        entityDao.update("user_device", condition, "status", Constants.STATUS_DISABLED);
      }

      condition = new HashMap<String, Object>();
      condition.put("userId", userId);
      condition.put("deviceId", deviceId);
      if (entityDao.exists("user_device", condition)) {
        Map<String, Object> updateValues = new HashMap<String, Object>();
        updateValues.put("registrationId", registrationId);
        updateValues.put("appVersion", appVersion);
        updateValues.put("phone", phone);
        updateValues.put("osVersion", osVersion);
        updateValues.put("network", network);
        updateValues.put("status", Constants.STATUS_ENABLED);
        entityDao.update("user_device", condition, updateValues);

        LOG.info("User " + userId + " updates registration id: " + registrationId);
        return ResponseBuilder.OK;
      }

      Entity entity = new Entity("user_device");
      entity.set("userId", userId).set("deviceId", deviceId).set("registrationId", registrationId);
      entity.set("appVersion", appVersion).set("phone", phone).set("osVersion", osVersion).set("network", network);
      entity.set("status", Constants.STATUS_ENABLED).set("createTime", time).set("updateTime", time);
      entityDao.save(entity);

      LOG.info("User " + userId + " submits registration id: " + registrationId);
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.info("Failed to submit registration id: " + registrationId, t);
      return ResponseBuilder.error(50002, "数据提交失败！");
    }
  }

}
