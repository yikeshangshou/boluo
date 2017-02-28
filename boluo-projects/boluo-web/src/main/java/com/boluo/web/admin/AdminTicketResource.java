package com.boluo.web.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
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

import com.boluo.dao.mapper.TicketRowMapper;
import com.boluo.model.Ticket;
import com.boluo.util.PageNumberUtils;
import com.boluo.util.Pair;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Jul 23, 2016
 */
@Path("/admin/ticket")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AdminTicketResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(AdminTicketResource.class);

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index(@QueryParam("status") int status, @QueryParam("page") @DefaultValue("1") int page) {
    page = page > 0 ? page : 1;

    Map<String, Object> condition = new HashMap<String, Object>();
    if (status != 0) {
      condition.put("status", status);
    }
    Pair<Integer, List<Ticket>> result = entityDao.findAndCount("ticket", condition, page, PageNumberUtils.PAGE_SIZE_MEDIUM, TicketRowMapper.getInstance());
    request.setAttribute("tickets", result.right);

    int count = result.left;
    Pair<List<Integer>, Integer> pages = PageNumberUtils.generate(page, count, PageNumberUtils.PAGE_SIZE_MEDIUM);
    request.setAttribute("currentPage", page);
    request.setAttribute("pages", pages.left);
    request.setAttribute("lastPage", pages.right);

    request.setAttribute("status", status);
    return Response.ok(new Viewable("index")).build();
  }

  @GET
  @Path("{id}/update_status")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> updateStatus(@PathParam("id") long id, @QueryParam("value") int value) {
    if (value != 1 && value != -3) {
      return ResponseBuilder.error(50000, "不允许的状态更新!");
    }

    try {
      entityDao.update("ticket", "id", id, "status", value);
      LOG.info("Update ticket status: " + value);
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.error("Failed to update ticket status: " + id, t);
      return ResponseBuilder.error(50000, "Failed to update ticket status: " + id);
    }
  }

}
