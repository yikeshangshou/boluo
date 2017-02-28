package com.boluo.web.api.v1;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.boluo.model.Entity;
import com.boluo.model.User;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Apr 29, 2016
 */
@Path("/api/v1/setting")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiSettingResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiSettingResource.class);
  private static final String[] SETTING_KEYS = new String[] { "emailOption", "pushOption", "SmsOption" };

  @GET
  @Path("setting")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getSetting() {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Entity entity = entityDao.get("user_setting", user.getId());
    return ResponseBuilder.ok(entity);
  }

  @POST
  @Path("setting")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> saveSetting(@FormParam("name") String name, @FormParam("value") String value) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (!ArrayUtils.contains(SETTING_KEYS, name)) {
      return ResponseBuilder.error(10202, "不支持的用户设置信息。");
    }

    if (StringUtils.equals("emailOption", name)) {
      if (StringUtils.isEmpty(value)) {
        return ResponseBuilder.error(10150, "邮箱设置不能为空。");
      }

    } else if (StringUtils.equals("pushOption", name)) {
      if (StringUtils.isEmpty(value)) {
        return ResponseBuilder.error(10151, "Push设置不能为空。");
      }

    } else if (StringUtils.equals("SmsOption", name)) {
      if (StringUtils.isEmpty(value)) {
        return ResponseBuilder.error(10152, "短信设置不能为空。");
      }

    } else {
      return ResponseBuilder.error(10202, "不支持的用户设置信息。");
    }

    LOG.info("User " + user.getId() + " save setting, " + name + ":" + value);
    long time = System.currentTimeMillis();
    if (entityDao.exists("user_setting", "id", user.getId())) {
      Map<String, Object> updateValues = new HashMap<String, Object>();
      updateValues.put(name, value);
      updateValues.put("updateTime", time);

      entityDao.update("user_setting", "id", user.getId(), updateValues);

    } else {
      Entity userSetting = new Entity("user_setting");
      userSetting.set("userId", user.getId()).set(name, value).set("createTime", time);
      entityDao.save(userSetting);
    }

    return ResponseBuilder.OK;
  }

}
