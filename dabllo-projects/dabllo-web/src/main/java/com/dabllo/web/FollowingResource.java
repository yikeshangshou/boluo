package com.dabllo.web;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.model.Topic;
import com.dabllo.model.User;
import com.dabllo.service.TopicService;
import com.dabllo.util.Pair;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Jun 15, 2016
 */
@Path("/following")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class FollowingResource extends BaseResource {

  @Resource
  TopicService topicService;

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index(@QueryParam("offset") @DefaultValue("0") long offset) {
    User user = getSessionUser();
    if (user == null) {
      return signinAndGoback();
    }

    // 已经没有下一页数据。
    if (offset < 0) {
      request.setAttribute("offset", -1);
      return Response.ok(new Viewable("index")).build();
    }

    Pair<Long, List<Topic>> pair = topicService.getFollowingTopics(user.getId(), offset);
    request.setAttribute("offset", pair.left);
    request.setAttribute("topics", pair.right);

    return Response.ok(new Viewable("index")).build();
  }

}
