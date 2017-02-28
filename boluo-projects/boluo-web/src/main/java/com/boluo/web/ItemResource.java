package com.boluo.web;

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

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.boluo.Constants;
import com.boluo.dao.BaseDao;
import com.boluo.dao.mapper.DiscussionRowMapper;
import com.boluo.dao.mapper.ItemRowMapper;
import com.boluo.dao.mapper.UserRowMapper;
import com.boluo.model.Discussion;
import com.boluo.model.Item;
import com.boluo.model.User;
import com.boluo.service.ItemService;
import com.boluo.util.Pair;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Jul 7, 2016
 */
@Path("/item")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ItemResource extends BaseResource {

  @Resource
  protected ItemService itemService;

  @GET
  @Path("{id}")
  @Produces(MediaType.TEXT_HTML)
  public Response get(@PathParam("id") long itemId) {
    Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
    if (item == null || !item.isEnabled()) {
      request.setAttribute("_blank", true);
      request.setAttribute("_error", "未找到资讯：资讯可能已经被删除。");
      return Response.ok(new Viewable("item")).build();
    }
    request.setAttribute("item", item);

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("itemId", itemId);
    condition.put("status", Constants.STATUS_ENABLED);
    List<Discussion> discussions = entityDao.find("discussion", condition, DiscussionRowMapper.getInstance());
    if (CollectionUtils.isNotEmpty(discussions)) {
      for (Discussion discussion : discussions) {
        setUserProperties(discussion);
        setDiscussionProperties(discussion, getSessionUserId());
      }
    }
    request.setAttribute("discussions", discussions);

    condition = new HashMap<String, Object>();
    condition.put("status", Constants.STATUS_ENABLED);
    Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
    long begin = System.currentTimeMillis() - 6 * Constants.HOUR;
    offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
    List<Item> items = entityDao.findByOffset("item", condition, offsets, "rank", BaseDao.ORDER_OPTION_DESC, 1, 5, ItemRowMapper.getInstance());
    request.setAttribute("items", items);

    return Response.ok(new Viewable("item")).build();
  }

  @GET
  @Path("{id}/read")
  @Produces(MediaType.TEXT_HTML)
  public Response read(@PathParam("id") long itemId, @QueryParam("value") @DefaultValue("1") int value) {
    Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
    if (item == null) {
      request.setAttribute("_error", "未找到资讯。");
      request.setAttribute("_blank", true);
      return Response.ok(new Viewable("item")).build();
    }
    if (item.getStatus() < Constants.STATUS_ENABLED) {
      request.setAttribute("_blank", true);
      request.setAttribute("_error", "已经删除的资讯。");
      return Response.ok(new Viewable("item")).build();
    }

    itemService.readItem(getSessionUserId(), itemId, Constants.SOURCE_WEB, value);
    return redirect(item.getLink());
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index(@QueryParam("time") String time, @QueryParam("page") int page) {
    List<Item> items = itemService.getItems(time, page);
    request.setAttribute("items", items);

    return Response.ok(new Viewable("index")).build();
  }

  @GET
  @Path("share")
  @Produces(MediaType.TEXT_HTML)
  public Response share() {
    User user = getSessionUser();
    if (user == null) {
      return signinAndGoback();
    }

    return Response.ok(new Viewable("share")).build();
  }

  private void setDiscussionProperties(Discussion discussion, long userId) {
    // Reply count
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("discussionId", discussion.getId());
    condition.put("status", Constants.STATUS_ENABLED);
    int count = entityDao.count("reply", condition);
    discussion.getProperties().put("replyCount", count);

    // Follow Count.
    condition = new HashMap<String, Object>();
    condition.put("discussionId", discussion.getId());
    condition.put("status", Constants.STATUS_ENABLED);
    count = entityDao.count("discussion_follow", condition);
    discussion.getProperties().put("followCount", count);

    if (userId > 0) {
      // Follow status.
      condition.put("userId", userId);
      if (entityDao.exists("discussion_follow", condition)) {
        discussion.getProperties().put("followStatus", 1);
      }
    }
  }

  private void setUserProperties(Discussion discussion) {
    User user = entityDao.get("user", discussion.getUserId(), UserRowMapper.getInstance());
    Map<String, Object> userValues = filterUser(user);
    discussion.getProperties().put("user", userValues);
  }

}
