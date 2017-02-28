package com.boluo.web.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import com.boluo.dao.mapper.CategoryRowMapper;
import com.boluo.model.Category;
import com.boluo.model.Entity;
import com.boluo.util.PageNumberUtils;
import com.boluo.util.Pair;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;
import com.sun.jersey.api.view.Viewable;

@Path("/admin/category")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AdminCategoryResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(AdminCategoryResource.class);

  @GET
  @Path("{id}/delete")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> delete(@PathParam("id") long id) {
    try {
      entityDao.delete("category", id);
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.error("Failed to delete category: " + id, t);
      return ResponseBuilder.error(101, "Failed to delete category: " + id);
    }
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index(@QueryParam("page") int page) {
    page = page > 0 ? page : 1;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("status", 1);
    Pair<Integer, List<Category>> result = entityDao.findAndCount("category", condition, page, PageNumberUtils.PAGE_SIZE_SMALL, CategoryRowMapper.getInstance());
    request.setAttribute("categories", result.right);

    condition.put("status", 1);
    condition.put("parentId", 0);
    List<Category> rootCategories = entityDao.find("category", condition, CategoryRowMapper.getInstance());
    request.setAttribute("rootCategories", rootCategories);

    int count = result.left;
    Pair<List<Integer>, Integer> pages = PageNumberUtils.generate(page, count, PageNumberUtils.PAGE_SIZE_SMALL);
    request.setAttribute("currentPage", page);
    request.setAttribute("pages", pages.left);
    request.setAttribute("lastPage", pages.right);
    return Response.ok(new Viewable("index")).build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@FormParam("id") long id, @FormParam("parentId") long parentId, @FormParam("slug") String slug, @FormParam("name") String name) {
    long time = System.currentTimeMillis();
    try {
      if (id > 0) {
        Map<String, Object> updateValues = new HashMap<String, Object>();
        updateValues.put("parentId", parentId);
        updateValues.put("slug", slug);
        updateValues.put("name", name);
        updateValues.put("updateTime", time);
        entityDao.update("category", "id", id, updateValues);

        updateValues.put("id", id);
        return ResponseBuilder.ok(updateValues);

      } else {
        Entity category = new Entity("category");
        category.set("parentId", parentId).set("slug", slug).set("name", name);
        category.set("status", 1).set("createTime", time);
        category = entityDao.saveAndReturn(category);

        return ResponseBuilder.ok(category);
      }

    } catch (Throwable t) {
      LOG.error("Failed to save category!", t);
      return ResponseBuilder.error(101, "Failed to save category!");
    }
  }

}
