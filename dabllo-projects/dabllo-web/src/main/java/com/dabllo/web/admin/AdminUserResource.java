package com.dabllo.web.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.dao.mapper.UserRowMapper;
import com.dabllo.model.User;
import com.dabllo.util.PageNumberUtils;
import com.dabllo.util.Pair;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Jul 20, 2016
 */
@Path("/admin/user")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AdminUserResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(AdminUserResource.class);

  @GET
  @Path("{id}")
  @Produces(MediaType.TEXT_HTML)
  public Response get(@PathParam("id") long id) {
    User user = entityDao.get("user", id, UserRowMapper.getInstance());
    if (user == null) {
      request.setAttribute("_blank", true);
      request.setAttribute("_error", "未找到资讯。");
    }

    request.setAttribute("user", user);
    return Response.ok(new Viewable("user")).build();
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index(@QueryParam("status") int status, @QueryParam("bindStatus") int bindStatus, @QueryParam("page") int page) {
    page = page > 0 ? page : 1;

    Map<String, Object> condition = new HashMap<String, Object>();
    if (status != 0) {
      condition.put("status", status);
    }
    if (bindStatus != 0) {
      condition.put("bindStatus", bindStatus);
    }
    Pair<Integer, List<User>> result = entityDao.findAndCount("user", condition, page, PageNumberUtils.PAGE_SIZE_MEDIUM, UserRowMapper.getInstance());
    request.setAttribute("users", result.right);

    int count = result.left;
    Pair<List<Integer>, Integer> pages = PageNumberUtils.generate(page, count, PageNumberUtils.PAGE_SIZE_MEDIUM);
    request.setAttribute("currentPage", page);
    request.setAttribute("pages", pages.left);
    request.setAttribute("lastPage", pages.right);

    request.setAttribute("status", status);
    request.setAttribute("bindStatus", bindStatus);
    return Response.ok(new Viewable("index")).build();
  }

  @GET
  @Path("{id}/update_status")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> updateStatus(@PathParam("id") long id, @QueryParam("value") int value) {
    if (value != 0 && value != 1 && value != -3) {
      return ResponseBuilder.error(50000, "不允许的状态更新!");
    }

    try {
      entityDao.update("user", "id", id, "status", value);
      LOG.info("Update user status: " + value);
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.error("Failed to update user status: " + id, t);
      return ResponseBuilder.error(50000, "Failed to update user status: " + id);
    }
  }

}
