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
import com.boluo.dao.mapper.ItemRowMapper;
import com.boluo.dao.mapper.TopicRowMapper;
import com.boluo.dao.mapper.UserRowMapper;
import com.boluo.model.Discussion;
import com.boluo.model.Entity;
import com.boluo.model.Item;
import com.boluo.model.Topic;
import com.boluo.model.User;
import com.boluo.service.DiscussionService;
import com.boluo.util.PageNumberUtils;
import com.boluo.util.Pair;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Jul 27, 2016
 */
@Path("/api/v1/discussion")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiDiscussionResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiDiscussionResource.class);

  @Resource
  protected DiscussionService discussionService;

  @GET
  @Path("{id}/delete")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> delete(@PathParam("id") long discussionId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (discussionId <= 0) {
      return ResponseBuilder.error(71404, "未找到讨论。");
    }
    Discussion discussion = entityDao.get("discussion", discussionId, DiscussionRowMapper.getInstance());
    if (discussion == null || discussion.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(71404, "未找到讨论。");
    }

    if (discussion.getUserId() != user.getId()) {
      return ResponseBuilder.ERR_PERMISSION_DENIED;
    }

    entityDao.update("discussion", "id", discussionId, "status", Constants.STATUS_DELETED_BY_USER);
    return ResponseBuilder.OK;
  }

  @GET
  @Path("{id}/follow")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> follow(@PathParam("id") long discussionId, @QueryParam("value") @DefaultValue("1") int value) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (discussionId <= 0) {
      return ResponseBuilder.error(71404, "未找到讨论。");
    }
    Discussion discussion = entityDao.get("discussion", discussionId, DiscussionRowMapper.getInstance());
    if (discussion == null || discussion.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(71404, "未找到讨论。");
    }

    int status = discussionService.followDiscussion(user.getId(), discussionId, value);
    return ResponseBuilder.ok(status);
  }

  @GET
  @Path("{id}")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> get(@PathParam("id") long discussionId) {
    if (discussionId <= 0) {
      return ResponseBuilder.error(71404, "未找到讨论。");
    }
    Discussion discussion = entityDao.get("discussion", discussionId, DiscussionRowMapper.getInstance());
    if (discussion == null || discussion.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(71404, "未找到讨论。");
    }

    setTopicOrItemProperties(discussion);
    setDiscussionProperties(discussion, getSessionUserId());
    return ResponseBuilder.ok(discussion);
  }

  @GET
  @Path("all")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getAllDiscussions(@QueryParam("offset") @DefaultValue("0") long offset, @QueryParam("size") @DefaultValue("10") int size) {
    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    size = (size > 0 && size < 50) ? size : PageNumberUtils.PAGE_SIZE_SMALL;

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("status", Constants.STATUS_ENABLED);
    List<Discussion> discussions = entityDao.findByOffset("discussion", condition, offset, size, DiscussionRowMapper.getInstance());
    if (CollectionUtils.isEmpty(discussions)) {
      // 没有查询到数据，直接返回。
      return ResponseBuilder.ok(null, -1);
    }

    for (Discussion discussion : discussions) {
      setTopicOrItemProperties(discussion);
      setDiscussionProperties(discussion, getSessionUserId());

      // 更新offset
      long id = discussion.getId();
      offset = offset > id ? id : offset;
    }

    return ResponseBuilder.ok(discussions, offset);
  }

  @GET
  @Path("following")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getFollowingDiscussions(@QueryParam("offset") @DefaultValue("0") long offset) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    Pair<Long, List<Discussion>> pair = discussionService.getFollowingDiscussions(user.getId(), offset);
    List<Discussion> discussions = pair.right;
    if (CollectionUtils.isNotEmpty(discussions)) {
      long readAllTime = 0L;
      Entity entity = entityDao.findOne("user_operation", "userId", user.getId());
      if (entity != null) {
        readAllTime = entity.getLong("readAllTime");
      }

      for (Discussion discussion : discussions) {
        setUserProperties(discussion);
        setTopicOrItemProperties(discussion);
        setDiscussionProperties(discussion, 0);
        discussion.getProperties().put("followStatus", 1);

        // 未读小红点属性。
        setUnreadProperty(discussion, user.getId(), readAllTime);
      }
    }
    return ResponseBuilder.ok(pair.right, pair.left);
  }

  @GET
  @Path("{id}/following_status")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getFollowingStatus(@PathParam("id") long discussionId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("userId", user.getId());
    condition.put("discussionId", discussionId);
    Entity entity = entityDao.findOne("discussion_follow", condition);
    if (entity == null || entity.getInt("status") < Constants.STATUS_ENABLED) {
      return ResponseBuilder.ok(0);

    } else {
      return ResponseBuilder.ok(1);
    }
  }

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getItemDiscussions(@QueryParam("itemId") long itemId, @QueryParam("page") @DefaultValue("1") int page) {
    page = page > 0 ? page : 1;

    if (itemId <= 0) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }
    Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
    if (item == null || item.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("itemId", itemId);
    condition.put("status", Constants.STATUS_ENABLED);
    List<Discussion> discussions = entityDao.find("discussion", condition, DiscussionRowMapper.getInstance());
    if (CollectionUtils.isNotEmpty(discussions)) {
      for (Discussion discussion : discussions) {
        setUserProperties(discussion);
        setTopicOrItemProperties(discussion);
        setDiscussionProperties(discussion, getSessionUserId());
      }
    }

    return ResponseBuilder.ok(discussions);
  }

  @GET
  @Path("mine")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getUserDiscussions(@QueryParam("offset") @DefaultValue("0") long offset) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    Pair<Long, List<Discussion>> pair = discussionService.getDiscussionsByUser(user.getId(), offset);
    List<Discussion> discussions = pair.right;
    if (CollectionUtils.isNotEmpty(discussions)) {
      for (Discussion discussion : discussions) {
        setTopicOrItemProperties(discussion);
        setDiscussionProperties(discussion, 0);
      }
    }

    return ResponseBuilder.ok(pair.right, pair.left);
  }

  @GET
  @Path("{id}/read")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> read(@PathParam("id") long discussionId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (discussionId <= 0) {
      return ResponseBuilder.error(71404, "未找到讨论。");
    }
    Discussion discussion = entityDao.get("discussion", discussionId, DiscussionRowMapper.getInstance());
    if (discussion == null) {
      return ResponseBuilder.error(71404, "未找到讨论。");
    }

    discussionService.readDiscussion(user.getId(), discussionId);
    return ResponseBuilder.OK;
  }

  @GET
  @Path("read_all")
  public Map<String, Object> readAll() {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    discussionService.readAllDiscussions(user.getId());
    return ResponseBuilder.OK;
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@FormParam("topicId") long topicId, @FormParam("itemId") long itemId, @FormParam("title") String title, @FormParam("description") String description) {
    long userId = getSessionUserId();

    Topic topic = null;
    if (topicId > 0) {
      topic = entityDao.get("topic", topicId, TopicRowMapper.getInstance());
      if (topic == null || !topic.isEnabled()) {
        return ResponseBuilder.error(30404, "未找到专题。");
      }
    }
    if (itemId > 0) {
      Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
      if (item == null || !item.isEnabled()) {
        return ResponseBuilder.error(70404, "未找到资讯。");
      }
    }

    if (StringUtils.isEmpty(title)) {
      return ResponseBuilder.error(71101, "请输入讨论标题。");
    }
    if (StringUtils.length(title) < 5) {
      return ResponseBuilder.error(71102, "讨论标题不能少于5个字。");
    }
    if (StringUtils.length(title) > 50) {
      return ResponseBuilder.error(71103, "讨论标题不能超过50个字。");
    }
    if (StringUtils.length(description) > 200) {
      return ResponseBuilder.error(71104, "讨论描述不能超过200个字。");
    }

    long time = System.currentTimeMillis();
    // Save discussion.
    Entity discussion = new Entity("discussion");
    discussion.set("userId", userId).set("topicId", topicId).set("itemId", itemId);
    discussion.set("title", title).set("description", description);
    discussion.set("status", 1).set("createTime", time);
    discussion = entityDao.saveAndReturn(discussion);

    long discussionId = discussion.getId();
    LOG.info("New discussion submitted: " + discussionId);

    // 加入关注列表。
    discussionService.followDiscussion(userId, discussionId, 1);
    if (topic != null) {
      String type = "topic";
      StringBuffer sb = new StringBuffer();
      sb.append("你关注的主题");
      sb.append(topic.getName());
      sb.append("有了新");
      sb.append("讨论 ：\"");
      sb.append(title);
      sb.append("\"");

      Entity entityNotice = new Entity("notification_user_text");
      entityNotice.set("content", sb.toString()).set("objectId", topicId).set("fromUserId", userId);
      entityNotice.set("status", Constants.STATUS_NO).set("createTime", time).set("type", type);
      entityDao.save(entityNotice);
    }

    return ResponseBuilder.ok(discussion.getId());
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

  private void setTopicOrItemProperties(Discussion discussion) {
    if (discussion.getTopicId() > 0) {
      Topic topic = entityDao.get("topic", discussion.getTopicId(), TopicRowMapper.getInstance());
      if (topic != null && topic.isEnabled()) {
        discussion.getProperties().put("topicId", topic.getId());
        discussion.getProperties().put("topicTitle", topic.getTitle());
      }
    }

    if (discussion.getItemId() > 0) {
      Item item = entityDao.get("item", discussion.getItemId(), ItemRowMapper.getInstance());
      if (item != null && item.isEnabled()) {
        discussion.getProperties().put("itemId", item.getId());
        discussion.getProperties().put("itemTitle", item.getTitle());
      }
    }
  }

  private void setUnreadProperty(Discussion discussion, long userId, long readAllTime) {
    if (discussion.getLastReplyTime() == 0 || discussion.getLastReplyTime() < readAllTime) {
      discussion.getProperties().put("unread", Constants.STATUS_NO);
      return;
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("userId", userId);
    condition.put("discussionId", discussion.getId());
    Entity entity = entityDao.findOne("discussion_read", condition);

    if (entity == null || entity.getLong("updateTime") < discussion.getLastReplyTime()) {
      discussion.getProperties().put("unread", Constants.STATUS_YES);

    } else {
      discussion.getProperties().put("unread", Constants.STATUS_NO);
    }
  }

  private void setUserProperties(Discussion discussion) {
    User user = entityDao.get("user", discussion.getUserId(), UserRowMapper.getInstance());
    Map<String, Object> userValues = filterUser(user);
    discussion.getProperties().put("user", userValues);
  }

}
