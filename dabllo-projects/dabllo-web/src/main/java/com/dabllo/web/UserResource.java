package com.dabllo.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.dao.mapper.UserRowMapper;
import com.dabllo.model.User;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Jun 15, 2016
 */
@Path("/user")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class UserResource extends BaseResource {

  @GET
  @Path("{id}")
  @Produces(MediaType.TEXT_HTML)
  public Response index(@PathParam("id") long id) {
    User user = entityDao.get("user", id, UserRowMapper.getInstance());
    if (user == null) {
      request.setAttribute("_blank", true);
      request.setAttribute("_error", "未找到用户。");
      return Response.ok(new Viewable("user")).build();
    }

    request.setAttribute("user", user);
    return Response.ok(new Viewable("user")).build();
  }

}
