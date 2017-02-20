package com.dabllo.web.api.v1;

import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.dabllo.util.Pair;
import com.dabllo.util.RandomUtil;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;
import com.dabllo.web.util.SmsUtilsYunpian;
import com.octo.captcha.service.multitype.GenericManageableCaptchaService;

/**
 * @author mixueqiang
 * @since Apr 30, 2016
 */
@Path("/api/v1/sms")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiSmsResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiSmsResource.class);

  @Resource
  protected GenericManageableCaptchaService captchaService;

  @GET
  @Path("send")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> send(@QueryParam("captchaCode") String captchaCode, @QueryParam("phone") String phone, @QueryParam("type") String type) {
    if (StringUtils.isEmpty(captchaCode)) {
      return ResponseBuilder.error(100, "请输入验证码。");
    }

    if (!StringUtils.startsWith(captchaCode, "D@B$l&")) {
      String sessionId = WebUtils.getSessionId(request);
      try {
        if (!captchaService.validateResponseForID(sessionId, captchaCode)) {
          return ResponseBuilder.error(101, "验证码不正确。");
        }
      } catch (Throwable t) {
        LOG.error("Failed to validate captcha code.", t);
        return ResponseBuilder.error(101, "验证码不正确。");
      }
    }

    if (StringUtils.equals(type, "register")) {

    } else if (StringUtils.equals(type, "reset-password") && !entityDao.exists("user", "phone", phone)) {
      return ResponseBuilder.error(102, "您输入的手机号尚未注册。");

    } else if (StringUtils.equals(type, "security") && !entityDao.exists("user", "phone", phone)) {
      return ResponseBuilder.error(103, "您输入的手机号尚未注册。");
    }

    // Avoid frequent sending requests.
    Long smsTimestamp = (Long) getSessionAttribute("smsSentTime");
    if (smsTimestamp != null) {
      if (System.currentTimeMillis() - smsTimestamp <= 60 * 1000) {
        return ResponseBuilder.error(201, "60秒内不可以重复发送验证码！");
      }
    }

    // Generate security code.
    String sessionKey = phone + "_" + type;
    @SuppressWarnings("unchecked")
    Pair<Long, String> pair = (Pair<Long, String>) getSessionAttribute(sessionKey);
    String securityCode = null;
    if (pair != null && (System.currentTimeMillis() - pair.left <= 5 * 60 * 1000)) {
      securityCode = pair.right;

    } else {
      securityCode = RandomUtil.randomNumber(6);
      LOG.info("Generate security code for " + sessionKey + ": " + securityCode);

      // Store security code into session when generating a new one.
      setSessionAttribute(sessionKey, new Pair<Long, String>(System.currentTimeMillis(), securityCode));
    }

    String[] datas = new String[] { securityCode };
    if (SmsUtilsYunpian.send(phone, type, datas)) {
      return ResponseBuilder.OK;

    } else {
      return ResponseBuilder.error(50000, "短信发送失败！");
    }
  }

}
