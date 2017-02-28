package com.boluo.web;

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

import com.boluo.model.Feed;
import com.boluo.model.User;
import com.boluo.service.DiscoveryService;
import com.boluo.util.Pair;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Jun 15, 2016
 */
@Path("/discovery")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DiscoveryResource extends BaseResource {

  @Resource
  DiscoveryService discoveryService;

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index(@QueryParam("offset") @DefaultValue("0") long offset) {
    // 已经没有下一页数据。
    if (offset < 0) {
      request.setAttribute("offset", -1);
      return Response.ok(new Viewable("index")).build();
    }

    long userId = 0;
    User user = getSessionUser();
    if (user != null) {
      userId = user.getId();
    }

    Pair<Long, List<Feed>> pair = discoveryService.getFeeds(userId, offset);
    request.setAttribute("offset", pair.left);
    request.setAttribute("feeds", pair.right);

    return Response.ok(new Viewable("index")).build();
  }

}
