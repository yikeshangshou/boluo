package com.dabllo.web.api.v1;

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

import com.dabllo.util.EncryptUtils;
import com.dabllo.util.Pair;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;

/**
 * @author mixueqiang
 * @since May 11, 2016
 */
@Path("/api/v1/password")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class Api̦PasswordResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(Api̦PasswordResource.class);

  @POST
  @Path("reset")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> reset(@FormParam("phone") String phone, @FormParam("securityCode") String securityCode, @FormParam("password") String password,
      @FormParam("confirmPassword") String confirmPassword) {
    // Data validation.
    if (StringUtils.isEmpty(phone)) {
      return ResponseBuilder.error(101, "请输入手机号。");
    }
    if (StringUtils.isNotEmpty(phone) && StringUtils.length(phone) != 11) {
      return ResponseBuilder.error(102, "请输入有效的手机号。");
    }
    if (StringUtils.isEmpty(password)) {
      return ResponseBuilder.error(103, "请输入密码。");
    }
    if (StringUtils.isNotEmpty(confirmPassword) && !StringUtils.equals(password, confirmPassword)) {
      return ResponseBuilder.error(104, "两次输入的密码不一致。");
    }
    String sessionKey = phone + "_reset_password";
    @SuppressWarnings("unchecked")
    Pair<Long, String> pair = (Pair<Long, String>) getSessionAttribute(sessionKey);
    if (!StringUtils.equals(securityCode, pair.right)) {
      return ResponseBuilder.error(105, "验证码错误。");
    }
    if (!entityDao.exists("user", "phone", phone)) {
      return ResponseBuilder.error(105, "您输入的手机号没有注册过，请注册后登录。");
    }

    try {
      // Update user password.
      entityDao.update("user", "phone", phone, "password", EncryptUtils.Md5(password, 10));
      LOG.info("User " + phone + " updates password OK.");
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.error("Error occurs when updating user password: " + phone, t);
      return ResponseBuilder.error(500, "重新设置密码失败，请稍后再试!");
    }
  }

}
