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

import com.dabllo.dao.BaseDao;
import com.dabllo.dao.mapper.ReplyRowMapper;
import com.dabllo.dao.mapper.UserRowMapper;
import com.dabllo.model.Reply;
import com.dabllo.model.User;
import com.dabllo.util.PageNumberUtils;
import com.dabllo.util.Pair;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Jul 30, 2016
 */
@Path("/admin/reply")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AdminReplyResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(AdminReplyResource.class);

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getReplies(@QueryParam("discussionId") long discussionId, @QueryParam("status") int status, @QueryParam("page") int page) {
    page = page > 0 ? page : 1;

    Map<String, Object> condition = new HashMap<String, Object>();
    if (discussionId > 0) {
      condition.put("discussionId", discussionId);
    }
    if (status != 0) {
      condition.put("status", status);
    }
    Pair<Integer, List<Reply>> result = entityDao.findAndCount("reply", condition, page, ReplyRowMapper.getInstance());

    List<Reply> replies = result.right;
    for (Reply reply : replies) {
      User user = entityDao.get("user", reply.getUserId(), UserRowMapper.getInstance());
      reply.getProperties().put("user", user);
    }

    request.setAttribute("discussionId", discussionId);
    request.setAttribute("replies", replies);

    int count = result.left;
    Pair<List<Integer>, Integer> pages = PageNumberUtils.generate(page, count, BaseDao.PAGE_SIZE_MEDIUM);
    request.setAttribute("currentPage", page);
    request.setAttribute("pages", pages.left);
    request.setAttribute("lastPage", pages.right);

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
      entityDao.update("reply", "id", id, "status", value);
      LOG.info("Update reply status: " + value);
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.error("Failed to update reply status: " + id, t);
      return ResponseBuilder.error(50000, "Failed to update reply status: " + id);
    }
  }

}
