package com.dabllo.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.Constants;
import com.dabllo.dao.BaseDao;
import com.dabllo.dao.mapper.ItemRowMapper;
import com.dabllo.model.Item;
import com.dabllo.service.impl.BaseService;
import com.dabllo.util.PageNumberUtils;
import com.dabllo.util.Pair;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Aug 15, 2016
 */
@Path("/daily")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DailyResource extends BaseResource {

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return getByDate(dateFormat.format(new Date()));
  }

  @GET
  @Path("{date}")
  @Produces(MediaType.TEXT_HTML)
  public Response getByDate(@PathParam("date") String date) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date theDay = null;
    try {
      theDay = dateFormat.parse(date);

    } catch (Throwable t) {
      request.setAttribute("_blank", true);
      request.setAttribute("_error", "日期格式错误！");
      return Response.ok(new Viewable("index")).build();
    }

    long begin = theDay.getTime();
    long end = begin + 1 * BaseService.DAY;

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("status", Constants.STATUS_ENABLED);

    Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
    offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
    offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_DESC), end);
    List<Item> rankedItems = entityDao.findByOffset("item", condition, offsets, "rank", BaseDao.ORDER_OPTION_DESC, 1, PageNumberUtils.PAGE_SIZE_SMALL, ItemRowMapper.getInstance());
    request.setAttribute("rankedItems", rankedItems);

    condition.put("hot", 1);
    List<Item> hotItems = entityDao.findByOffset("item", condition, offsets, "rank", BaseDao.ORDER_OPTION_DESC, 1, PageNumberUtils.PAGE_SIZE_SMALL, ItemRowMapper.getInstance());
    request.setAttribute("hotItems", hotItems);

    request.setAttribute("date", date);
    return Response.ok(new Viewable("index")).build();
  }

}
