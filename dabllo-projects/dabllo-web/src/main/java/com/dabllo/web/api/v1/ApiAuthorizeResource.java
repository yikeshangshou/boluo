package com.dabllo.web.api.v1;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.model.Entity;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Jun 29, 2016
 */
@Path("/api/v1/authorize")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiAuthorizeResource extends BaseResource {

  @GET
  @Path("weibo")
  @Produces(MediaType.TEXT_HTML)
  public Response weibo() {
    StringBuilder sb = new StringBuilder("https://api.weibo.com/oauth2/authorize?scope=all&response_type=code&client_id=4029652859&redirect_uri=");
    sb.append("http://www.aqwa.cn/api/v1/authorize/weibo/callback");

    return redirect(sb.toString());
  }

  @GET
  @Path("weibo/callback")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> weiboCallback(@QueryParam("code") String code) {
    long time = System.currentTimeMillis();
    Entity entity = new Entity("user_external");
    entity.set("source", "weibo").set("code", code);
    entity.set("status", 1).set("createTime", time);
    entityDao.save(entity);

    return ResponseBuilder.OK;
  }

  @GET
  @Path("weibo/cancel")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> weiboCancel() {
    return ResponseBuilder.ERR_UNSUPPORTED_API;
  }

}
