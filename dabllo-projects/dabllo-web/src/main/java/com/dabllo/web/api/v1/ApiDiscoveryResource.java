package com.dabllo.web.api.v1;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.model.Feed;
import com.dabllo.model.User;
import com.dabllo.service.DiscoveryService;
import com.dabllo.util.Pair;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Jun 14, 2016
 */
@Path("/api/v1/discovery")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiDiscoveryResource extends BaseResource {

  @Resource
  DiscoveryService discoveryService;

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getFeeds(@QueryParam("offset") @DefaultValue("0") long offset) {
    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    long userId = 0;
    User user = getSessionUser();
    if (user != null) {
      userId = user.getId();
    }

    Pair<Long, List<Feed>> pair = discoveryService.getFeeds(userId, offset);
    return ResponseBuilder.ok(pair.right, pair.left);
  }

}
