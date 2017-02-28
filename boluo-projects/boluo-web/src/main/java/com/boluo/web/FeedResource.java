package com.boluo.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.boluo.dao.mapper.TopicRowMapper;
import com.boluo.model.Topic;
import com.boluo.model.User;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Jul 7, 2016
 */
@Path("/feed")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class FeedResource extends BaseResource {

  @GET
  @Path("share")
  @Produces(MediaType.TEXT_HTML)
  public Response share(@QueryParam("topicId") long topicId) {
    User user = getSessionUser();
    if (user == null) {
      return signinAndGoback();
    }

    Topic topic = entityDao.get("topic", topicId, TopicRowMapper.getInstance());
    if (topic == null) {
      request.setAttribute("_blank", true);
      request.setAttribute("_error", "未找到主题。");
      return Response.ok(new Viewable("share")).build();
    }

    request.setAttribute("topic", topic);
    return Response.ok(new Viewable("share")).build();
  }

}
