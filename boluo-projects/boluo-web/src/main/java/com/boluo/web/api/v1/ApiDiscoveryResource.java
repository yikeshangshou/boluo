package com.boluo.web.api.v1;

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

import com.boluo.model.Feed;
import com.boluo.model.User;
import com.boluo.service.DiscoveryService;
import com.boluo.util.Pair;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;

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
