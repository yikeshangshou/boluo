package com.dabllo.web.admin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.web.BaseResource;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Mar 7, 2014
 */
@Path("/admin")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AdminIndexResource extends BaseResource {

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index() {
    return Response.ok(new Viewable("index")).build();
  }

}
