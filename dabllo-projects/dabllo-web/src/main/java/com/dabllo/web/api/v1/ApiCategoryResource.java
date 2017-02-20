package com.dabllo.web.api.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.dao.mapper.CategoryRowMapper;
import com.dabllo.model.Category;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Apr 29, 2016
 */
@Path("/api/v1/category")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiCategoryResource extends BaseResource {

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> get() {
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("parentId", 0);
    condition.put("status", 1);

    List<Category> categories = entityDao.find("category", condition, CategoryRowMapper.getInstance());
    return ResponseBuilder.ok(categories);
  }

}
