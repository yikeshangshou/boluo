package com.boluo.web;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.boluo.dao.BaseDao;
import com.boluo.dao.mapper.CategoryRowMapper;
import com.boluo.dao.mapper.TopicRowMapper;
import com.boluo.model.Category;
import com.boluo.model.Topic;
import com.boluo.service.SessionService;
import com.boluo.util.PageNumberUtils;
import com.boluo.util.Pair;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Oct 12, 2014
 */
@Path("/")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class IndexResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(IndexResource.class);

  @Resource
  protected SessionService sessionService;

  @GET
  @Path("{slug}")
  @Produces(MediaType.TEXT_HTML)
  public Response getByCategory(@PathParam("slug") String slug, @QueryParam("p") @DefaultValue("1") int page) {
    page = page > 0 ? page : 1;
    Category category = entityDao.findOne("category", "slug", slug, CategoryRowMapper.getInstance());
    if (category == null) {
      return Response.ok(new Viewable("topics")).build();
    }

    request.setAttribute("category", category);
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("categoryId", category.getId());
    condition.put("status", 1);
    Pair<Integer, List<Topic>> result = entityDao.findAndCount("topic", condition, page, TopicRowMapper.getInstance(), BaseDao.ORDER_OPTION_DESC);
    request.setAttribute("topics", result.right);

    Pair<List<Integer>, Integer> pages = PageNumberUtils.generate(page, result.left);
    request.setAttribute("currentPage", page);
    request.setAttribute("pages", pages.left);
    request.setAttribute("lastPage", pages.right);

    return Response.ok(new Viewable("topics")).build();
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index() {
    return Response.ok(new Viewable("index")).build();
  }

  @GET
  @Path("signin")
  @Produces(MediaType.TEXT_HTML)
  public Response signin() {
    return Response.ok(new Viewable("signin")).build();
  }

  @GET
  @Path("signout")
  @Produces(MediaType.TEXT_HTML)
  public Response signout() {
    try {
      // Clear sessionId.
      String sessionId = WebUtils.getSessionId(request);
      if (StringUtils.isNotEmpty(sessionId)) {
        sessionService.destorySession(sessionId);
      }

      request.getSession().invalidate();
      return Response.seeOther(new URI("/")).build();

    } catch (Throwable t) {
      LOG.error("User can not sign out.", t);
      return null;
    }
  }

  @GET
  @Path("signup")
  @Produces(MediaType.TEXT_HTML)
  public Response signup() {
    return Response.ok(new Viewable("signup")).build();
  }

}
