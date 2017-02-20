package com.dabllo.web.api.v1;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Apr 27, 2016
 */
@Path("/api/v1/ping")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiPingResource extends BaseResource {
  private static final Map<String, Integer> HOSTS = new HashMap<String, Integer>();

  static {
    HOSTS.put("www.aqwa.cn:80", 0);
    HOSTS.put("120.55.99.96:80", 10);
  }

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> ping() {
    return ResponseBuilder.ok(HOSTS);
  }

}
