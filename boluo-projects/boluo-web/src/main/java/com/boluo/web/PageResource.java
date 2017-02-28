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
 * @since Jul 1, 2016
 */
@Path("/page")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class PageResource extends BaseResource {

  @GET
  @Path("about")
  @Produces(MediaType.TEXT_HTML)
  public Response about() {
    return Response.ok(new Viewable("about")).build();
  }

  @GET
  @Path("reset_password")
  @Produces(MediaType.TEXT_HTML)
  public Response resetPassword() {
    return Response.ok(new Viewable("reset_password")).build();
  }

  @GET
  @Path("result_ok")
  @Produces(MediaType.TEXT_HTML)
  public Response resultOK() {
    return Response.ok(new Viewable("result_ok")).build();
  }

}
