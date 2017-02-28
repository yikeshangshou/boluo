package com.boluo.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since May 11, 2016
 */
@Path("/password")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class PasswordResource extends BaseResource {

  @GET
  @Path("reset")
  @Produces(MediaType.TEXT_HTML)
  public Response reset() {
    return Response.ok(new Viewable("reset")).build();
  }
  
}
