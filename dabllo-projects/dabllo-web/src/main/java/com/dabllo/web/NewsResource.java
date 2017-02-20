package com.dabllo.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.Constants;
import com.dabllo.dao.BaseDao;
import com.dabllo.dao.mapper.ItemRowMapper;
import com.dabllo.model.Entity;
import com.dabllo.model.Item;
import com.dabllo.service.SectionService;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Jul 29, 2016
 */
@Path("/news")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class NewsResource extends BaseResource {

  @GET
  @Path("{date}")
  @Produces(MediaType.TEXT_HTML)
  public Response getNews(@PathParam("date") String date) {
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("date", date);
    List<Entity> entities = entityDao.find("section_" + SectionService.TIME_1D, condition, 1, 10, "rank", BaseDao.ORDER_OPTION_DESC);
    if (CollectionUtils.isEmpty(entities)) {
      request.setAttribute("_blank", true);
      request.setAttribute("_error", "未找到这一天的资讯。");
      return Response.ok(new Viewable("news")).build();
    }

    List<Item> items = new ArrayList<Item>();
    for (Entity entity : entities) {
      long itemId = entity.getLong("itemId");
      Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
      if (item != null && item.getStatus() == Constants.STATUS_ENABLED) {
        items.add(item);
      }
    }

    request.setAttribute("date", date);
    request.setAttribute("news", items);
    return Response.ok(new Viewable("news")).build();
  }

}
