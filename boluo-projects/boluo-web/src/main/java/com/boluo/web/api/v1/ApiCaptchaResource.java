package com.boluo.web.api.v1;

import java.awt.image.BufferedImage;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.boluo.web.BaseResource;
import com.octo.captcha.service.multitype.GenericManageableCaptchaService;

/**
 * @author mixueqiang
 * @since Aug 1, 2016
 */
@Path("/api/v1/captcha")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiCaptchaResource extends BaseResource {

  @Resource
  protected GenericManageableCaptchaService captchaService;

  @GET
  @Produces("image/jpeg")
  public Response get() {
    String sessionId = WebUtils.getSessionId(request);
    BufferedImage challenge = (BufferedImage) captchaService.getChallengeForID(sessionId);

    return Response.ok(challenge).build();
  }

}
