package com.dabllo.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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
import com.dabllo.dao.mapper.DiscussionRowMapper;
import com.dabllo.dao.mapper.ItemRowMapper;
import com.dabllo.dao.mapper.ReplyRowMapper;
import com.dabllo.dao.mapper.UserRowMapper;
import com.dabllo.model.Discussion;
import com.dabllo.model.Item;
import com.dabllo.model.Reply;
import com.dabllo.model.User;
import com.dabllo.service.DiscussionService;
import com.dabllo.util.Pair;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Aug 10, 2016
 */
@Path("/discussion")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DiscussionResource extends BaseResource {

  @Resource
  protected DiscussionService discussionService;

  @GET
  @Path("{id}")
  @Produces(MediaType.TEXT_HTML)
  public Response get(@PathParam("id") long discussionId) {
    Discussion discussion = entityDao.get("discussion", discussionId, DiscussionRowMapper.getInstance());
    if (discussion == null || !discussion.isEnabled()) {
      request.setAttribute("_blank", true);
      request.setAttribute("_error", "未找到讨论：讨论可能已经被删除。");
      return Response.ok(new Viewable("discussion")).build();
    }
    request.setAttribute("discussion", discussion);

    Item item = entityDao.get("item", discussion.getItemId(), ItemRowMapper.getInstance());
    request.setAttribute("item", item);

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("discussionId", discussionId);
    condition.put("status", 1);
    List<Reply> replies = entityDao.find("reply", condition, ReplyRowMapper.getInstance());
    for (Reply reply : replies) {
      setUserProperties(reply);
      setReplyProperties(reply, getSessionUserId());
    }
    request.setAttribute("replies", replies);

    condition = new HashMap<String, Object>();
    condition.put("status", Constants.STATUS_ENABLED);
    Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
    long begin = System.currentTimeMillis() - 6 * Constants.HOUR;
    offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
    List<Item> items = entityDao.findByOffset("item", condition, offsets, "rank", BaseDao.ORDER_OPTION_DESC, 1, 5, ItemRowMapper.getInstance());
    request.setAttribute("items", items);

    return Response.ok(new Viewable("discussion")).build();
  }

  private void setReplyProperties(Reply reply, long userId) {
    // Up count.
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("replyId", reply.getId());
    condition.put("value", 1);
    condition.put("status", Constants.STATUS_ENABLED);
    int count = entityDao.count("reply_up", condition);
    reply.getProperties().put("upCount", count);

    if (userId > 0) { // 当前用户的赞同、反对状态。
      condition = new HashMap<String, Object>();
      condition.put("replyId", reply.getId());
      condition.put("userId", userId);
      condition.put("value", 1);
      // Up status.
      if (entityDao.exists("reply_up", condition)) {
        reply.getProperties().put("upStatus", 1);
      }

      // Down status.
      if (entityDao.exists("reply_down", condition)) {
        reply.getProperties().put("downStatus", 1);
      }
    }
  }

  private void setUserProperties(Reply reply) {
    User user = entityDao.get("user", reply.getUserId(), UserRowMapper.getInstance());
    Map<String, Object> userValues = filterUser(user);
    reply.getProperties().put("user", userValues);
  }

}
