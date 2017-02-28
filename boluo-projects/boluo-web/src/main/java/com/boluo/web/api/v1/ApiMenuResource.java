package com.boluo.web.api.v1;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;
import com.boluo.web.util.Menus;

/**
 * @author mixueqiang
 * @since Aug 6, 2016
 */
@Path("/api/v1/menu")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiMenuResource extends BaseResource {

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getMenus() {
    return ResponseBuilder.ok(Menus.get());
  }

}
