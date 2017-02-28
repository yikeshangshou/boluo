package com.boluo.web.api.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.boluo.Constants;
import com.boluo.dao.mapper.DiscussionRowMapper;
import com.boluo.dao.mapper.ReplyRowMapper;
import com.boluo.dao.mapper.UserRowMapper;
import com.boluo.model.Discussion;
import com.boluo.model.Entity;
import com.boluo.model.Reply;
import com.boluo.model.User;
import com.boluo.service.AnonymousService;
import com.boluo.service.ReplyService;
import com.boluo.util.Pair;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;
import com.boluo.web.util.RequesetUtils;

/**
 * @author mixueqiang
 * @since Jul 27, 2016
 */
@Path("/api/v1/reply")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiReplyResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiReplyResource.class);

  @Resource
  protected ReplyService replyService;
  @Resource
  protected AnonymousService anonymousService;

  @GET
  @Path("{id}")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> get(@PathParam("id") long replyId) {
    if (replyId <= 0) {
      return ResponseBuilder.error(72404, "未找到观点。");
    }
    Reply reply = entityDao.get("reply", replyId, ReplyRowMapper.getInstance());
    if (reply == null || reply.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(72404, "未找到观点。");
    }

    return ResponseBuilder.ok(reply);
  }

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> get(@QueryParam("discussionId") long discussionId, @QueryParam("page") @DefaultValue("1") int page) {
    page = page > 0 ? page : 1;

    if (discussionId <= 0) {
      return ResponseBuilder.error(71404, "未找到讨论。");
    }
    Discussion discussion = entityDao.get("discussion", discussionId, DiscussionRowMapper.getInstance());
    if (discussion == null || discussion.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(71404, "未找到讨论。");
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("discussionId", discussionId);
    condition.put("status", 1);
    List<Reply> replies = entityDao.find("reply", condition, ReplyRowMapper.getInstance());
    for (Reply reply : replies) {
      setUserProperties(reply);
      setReplyProperties(reply, getSessionUserId());
    }

    return ResponseBuilder.ok(replies);
  }

  @GET
  @Path("mine")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getUserReplies(@QueryParam("offset") @DefaultValue("0") long offset) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    Pair<Long, List<Reply>> pair = replyService.getUserReplies(user.getId(), offset);
    List<Reply> replies = pair.right;
    if (CollectionUtils.isNotEmpty(replies)) {
      for (Reply reply : replies) {
        setDiscussionProperties(reply);
        setReplyProperties(reply, 0);
      }
    }

    return ResponseBuilder.ok(pair.right, pair.left);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@FormParam("discussionId") long discussionId, @FormParam("content") String content) {
    long userId = getSessionUserId();

    if (discussionId <= 0) {
      return ResponseBuilder.error(71404, "未找到讨论。");
    }
    Discussion discussion = entityDao.get("discussion", discussionId, DiscussionRowMapper.getInstance());
    if (discussion == null || discussion.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(71404, "未找到讨论。");
    }

    if (StringUtils.isEmpty(content)) {
      return ResponseBuilder.error(72101, "请输入内容。");
    }
    if (StringUtils.length(content) > 1000) {
      return ResponseBuilder.error(72103, "内容不能超过1000个字。");
    }

    long time = System.currentTimeMillis();
    // Save reply.
    Entity reply = new Entity("reply");
    reply.set("userId", userId).set("itemId", discussion.getItemId()).set("discussionId", discussionId);
    reply.set("content", content);
    reply.set("status", Constants.STATUS_ENABLED).set("createTime", time);
    reply = entityDao.saveAndReturn(reply);

    // Update last reply time.
    entityDao.update("discussion", "id", discussionId, "lastReplyTime", time);
    noticeNewReplySave(content, userId, time, discussionId);

    LOG.info("New discussion reply submitted: " + reply.getId());
    return ResponseBuilder.ok(reply.getId());
  }

  private void noticeNewReplySave(String content, long userId, long time, long discussionId) {
    String type = "replySave";
    Entity entity = new Entity("notification_user_text");
    entity.set("fromUserId", userId);
    entity.set("content", content).set("objectId", discussionId);
    entity.set("status", Constants.STATUS_NO).set("createTime", time).set("type", type);
    entityDao.save(entity);

  }

  @GET
  @Path("{id}/up")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> up(@PathParam("id") long replyId, @QueryParam("value") @DefaultValue("1") int value) {
    User user = getSessionUser();
    if (user == null) {
      String ip = RequesetUtils.getClientIp(request);

      int count = 0;
      if (StringUtils.isNotBlank(ip)) {
        anonymousService.upReply(ip, replyId);
        count = anonymousService.countReplyUp(ip, replyId);

      } else {
        LOG.warn("Can not get client IP for anonymous reply up.");
      }

      return ResponseBuilder.ok(count);
    }

    if (replyId <= 0) {
      return ResponseBuilder.error(72404, "未找到观点。");
    }
    Reply reply = entityDao.get("reply", replyId, ReplyRowMapper.getInstance());
    if (reply == null) {
      return ResponseBuilder.error(72404, "未找到观点。");
    }

    long userId = user.getId();
    value = value > 0 ? 1 : 0;
    long time = System.currentTimeMillis();
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("replyId", replyId);
    condition.put("userId", userId);
    if (entityDao.exists("reply_up", condition)) {
      Map<String, Object> updateValues = new HashMap<String, Object>();
      updateValues.put("value", value);
      updateValues.put("updateTime", time);
      entityDao.update("reply_up", condition, updateValues);

    } else {
      Entity up = new Entity("reply_up");
      up.set("replyId", replyId).set("userId", userId).set("value", value);
      up.set("status", Constants.STATUS_ENABLED).set("createTime", time);
      entityDao.save(up);

      if (value == 1 && userId != reply.getUserId()) {
        notifyNewReplyUp(reply, user, time);
      }
    }

    // 清理reply_down。
    if (value == 1) {
      condition.put("value", 1);
      if (entityDao.exists("reply_down", condition)) {
        Map<String, Object> updateValues = new HashMap<String, Object>();
        updateValues.put("value", 0);
        updateValues.put("updateTime", time);
        entityDao.update("reply_down", condition, updateValues);
      }
    }

    notifyReplyUpCountTask(reply, userId);
    return ResponseBuilder.ok(value);
  }

  @GET
  @Path("{id}/up_count")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> upCount(@PathParam("id") long replyId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (replyId <= 0) {
      return ResponseBuilder.error(72404, "未找到观点。");
    }
    Reply reply = entityDao.get("reply", replyId, ReplyRowMapper.getInstance());
    if (reply == null || reply.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(72404, "未找到观点。");
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("replyId", replyId);
    condition.put("value", 1);
    condition.put("status", 1);
    int count = entityDao.count("reply_up", condition);

    return ResponseBuilder.ok(count);
  }

  @GET
  @Path("{id}/down")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> down(@PathParam("id") long replyId, @QueryParam("value") @DefaultValue("1") int value) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (replyId <= 0) {
      return ResponseBuilder.error(72404, "未找到观点。");
    }
    Reply reply = entityDao.get("reply", replyId, ReplyRowMapper.getInstance());
    if (reply == null || reply.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(72404, "未找到观点。");
    }

    value = value > 0 ? 1 : 0;
    long time = System.currentTimeMillis();
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("replyId", replyId);
    condition.put("userId", user.getId());
    if (entityDao.exists("reply_down", condition)) {
      Map<String, Object> updateValues = new HashMap<String, Object>();
      updateValues.put("value", value);
      updateValues.put("updateTime", time);
      entityDao.update("reply_down", condition, updateValues);

    } else {
      Entity up = new Entity("reply_down");
      up.set("replyId", replyId).set("userId", user.getId()).set("value", value);
      up.set("status", Constants.STATUS_ENABLED).set("createTime", time);
      entityDao.save(up);
    }

    // 清理reply_up。
    if (value == 1) {
      condition.put("value", 1);
      if (entityDao.exists("reply_up", condition)) {
        Map<String, Object> updateValues = new HashMap<String, Object>();
        updateValues.put("value", 0);
        updateValues.put("updateTime", time);
        entityDao.update("reply_up", condition, updateValues);
      }
    }

    notifyReplyUpCountTask(reply, user.getId());
    return ResponseBuilder.ok(value);
  }

  @GET
  @Path("{id}/delete")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> delete(@PathParam("id") long replyId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (replyId <= 0) {
      return ResponseBuilder.error(72404, "未找到观点。");
    }
    Reply reply = entityDao.get("reply", replyId, ReplyRowMapper.getInstance());
    if (reply == null || reply.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(72404, "未找到观点。");
    }
    if (reply.getUserId() != user.getId()) {
      return ResponseBuilder.ERR_PERMISSION_DENIED;
    }

    entityDao.update("reply", "id", replyId, "status", Constants.STATUS_DELETED_BY_USER);
    return ResponseBuilder.OK;
  }

  private void setDiscussionProperties(Reply reply) {
    // Discussion title.
    Discussion discussion = entityDao.get("discussion", reply.getDiscussionId(), DiscussionRowMapper.getInstance());
    if (discussion != null) {
      reply.getProperties().put("title", discussion.getTitle());
    }
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

  private void notifyNewReplyUp(Reply reply, User fromUser, long upTime) {
    long fromUserId = fromUser.getId();
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("type", 72001);
    condition.put("dataId", reply.getId());

    if (entityDao.exists("notification_message", condition)) {
      Map<String, Object> updateValues = new HashMap<String, Object>();
      updateValues.put("status", Constants.STATUS_DISABLED);
      updateValues.put("lastUpTime", upTime);
      updateValues.put("updateTime", upTime);
      entityDao.update("notification_message", condition, updateValues);

    } else {
      Entity entity = new Entity("notification_message");
      entity.set("type", 72001).set("fromUserId", fromUserId).set("toUserId", reply.getUserId());
      entity.set("discussionId", reply.getDiscussionId()).set("replyId", reply.getId());
      entity.set("dataId", reply.getId()).set("content", reply.getContentPreview()).set("value", 1);
      entity.set("lastReadTime", 0).set("lastUpTime", upTime);
      entity.set("status", Constants.STATUS_ENABLED).set("createTime", upTime);
      entityDao.save(entity);
    }

    // Push notification.
    Entity entity = new Entity("notification_message_up");
    entity.set("type", 72001).set("fromUserId", fromUserId).set("fromUsername", fromUser.getUsername()).set("toUserId", reply.getUserId());
    entity.set("dataId", reply.getId()).set("content", reply.getContentPreview()).set("discussionId", reply.getDiscussionId());
    entity.set("status", Constants.STATUS_NO).set("createTime", upTime);
    entityDao.save(entity);
  }

  private void notifyReplyUpCountTask(Reply reply, long fromUserId) {
    long time = System.currentTimeMillis();

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("type", 72001);
    condition.put("dataId", reply.getId());

    if (entityDao.exists("notification_message", condition)) {
      Map<String, Object> updateValues = new HashMap<String, Object>();
      updateValues.put("status", Constants.STATUS_DISABLED);
      updateValues.put("updateTime", time);
      entityDao.update("notification_message", condition, updateValues);

    } else {
      Entity entity = new Entity("notification_message");
      entity.set("type", 72001).set("fromUserId", fromUserId).set("toUserId", reply.getUserId());
      entity.set("discussionId", reply.getDiscussionId()).set("replyId", reply.getId());
      entity.set("dataId", reply.getId()).set("content", reply.getContentPreview()).set("value", 1).set("lastReadTime", 0);
      entity.set("status", Constants.STATUS_ENABLED).set("createTime", time);
      entityDao.save(entity);
    }
  }

}
