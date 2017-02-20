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

import com.dabllo.dao.mapper.DiscussionRowMapper;
import com.dabllo.dao.mapper.UserRowMapper;
import com.dabllo.model.Discussion;
import com.dabllo.model.User;
import com.dabllo.util.PageNumberUtils;
import com.dabllo.util.Pair;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Jul 29, 2016
 */
@Path("/admin/discussion")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AdminDiscussionResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(AdminDiscussionResource.class);

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getDiscussions(@QueryParam("itemId") long itemId, @QueryParam("status") int status, @QueryParam("page") int page) {
    page = page > 0 ? page : 1;

    Map<String, Object> condition = new HashMap<String, Object>();
    if (itemId > 0) {
      condition.put("itemId", itemId);
    }
    if (status != 0) {
      condition.put("status", status);
    }
    Pair<Integer, List<Discussion>> result = entityDao.findAndCount("discussion", condition, page, DiscussionRowMapper.getInstance());

    List<Discussion> discussions = result.right;
    for (Discussion discussion : discussions) {
      User user = entityDao.get("user", discussion.getUserId(), UserRowMapper.getInstance());
      discussion.getProperties().put("user", user);

      discussion.getProperties().put("replyCount", 0);
    }

    request.setAttribute("status", status);
    request.setAttribute("itemId", itemId);
    request.setAttribute("discussions", discussions);

    int count = result.left;
    Pair<List<Integer>, Integer> pages = PageNumberUtils.generate(page, count, PageNumberUtils.PAGE_SIZE_MEDIUM);
    request.setAttribute("currentPage", page);
    request.setAttribute("pages", pages.left);
    request.setAttribute("lastPage", pages.right);

    return Response.ok(new Viewable("index")).build();
  }

  @GET
  @Path("{id}/update_status")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> updateStatus(@PathParam("id") long id, @QueryParam("value") int value) {
    if (value != 1 && value != 0 && value != -3) {
      return ResponseBuilder.error(50000, "不允许的状态更新!");
    }

    try {
      entityDao.update("discussion", "id", id, "status", value);
      LOG.info("Update discussion status: " + value);
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.error("Failed to update discussion status: " + id, t);
      return ResponseBuilder.error(50000, "Failed to update discussion status: " + id);
    }
  }

}
