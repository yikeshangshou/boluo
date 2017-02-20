package com.dabllo.web.api.v1;

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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.Constants;
import com.dabllo.dao.mapper.CommentRowMapper;
import com.dabllo.dao.mapper.FeedRowMapper;
import com.dabllo.dao.mapper.UserRowMapper;
import com.dabllo.model.Comment;
import com.dabllo.model.Entity;
import com.dabllo.model.Feed;
import com.dabllo.model.User;
import com.dabllo.util.PageNumberUtils;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;

/**
 * @author mixueqiang
 * @since May 26, 2016
 */
@Path("/api/v1/comment")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiCommentResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiCommentResource.class);

  @GET
  @Path("{id}")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getComment(@PathParam("id") long id) {
    Comment comment = entityDao.get("comment", id, CommentRowMapper.getInstance());
    return ResponseBuilder.ok(comment);
  }

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getComments(@QueryParam("feedId") long feedId, @QueryParam("offset") long offset) {
    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("status", 1);
    condition.put("feedId", feedId);

    List<Comment> comments = entityDao.findByOffset("comment", condition, offset, PageNumberUtils.PAGE_SIZE_MEDIUM, CommentRowMapper.getInstance());
    if (CollectionUtils.isEmpty(comments)) {
      // 没有查询到数据，直接返回。
      return ResponseBuilder.ok(null, -1);
    }

    for (Comment comment : comments) {
      setCommentUserProperties(comment);

      // 更新offset：取最小的一个 id 为下一次查询的 offset。
      long id = comment.getId();
      offset = offset > id ? id : offset;
    }

    return ResponseBuilder.ok(comments, offset);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@FormParam("feedId") String feedId, @FormParam("replyUserId") long replyUserId, @FormParam("content") String content) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (StringUtils.isEmpty(content)) {
      return ResponseBuilder.error(60101, "请输入评论内容。");
    }
    if (StringUtils.length(content) > 200) {
      return ResponseBuilder.error(60102, "评论内容不能超过200个字。");
    }

    Feed feed = entityDao.get("feed", feedId, FeedRowMapper.getInstance());
    if (feed == null) {
      return ResponseBuilder.error(60404, "未找到评论的动态。您评论的动态可能已经被删除。");
    }

    long time = System.currentTimeMillis();
    // Save comment.
    Entity comment = new Entity("comment");
    comment.set("userId", user.getId()).set("feedId", feedId).set("content", content).set("replyUserId", replyUserId);
    comment.set("status", 1).set("createTime", time);
    comment = entityDao.saveAndReturn(comment);

    // TODO: 修改成异步更新数量数据。
    Entity entity = new Entity("feed_comment");
    entity.set("feedId", feedId).set("commentId", comment.getId());
    entity.set("status", 1).set("createTime", time);
    entityDao.save(entity);

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("feedId", feedId);
    condition.put("status", 1);
    int commentCount = entityDao.count("feed_comment", condition);
    entityDao.update("feed", "id", feedId, "commentCount", commentCount);

    // 记录用户参与动态的信息。
    condition = new HashMap<String, Object>();
    condition.put("feedId", feedId);
    condition.put("userId", user.getId());
    if (entityDao.exists("feed_user", condition)) {
      // 更新用户参与一个动态的时间。
      entityDao.update("feed_user", condition, "updateTime", time);

    } else {
      // 新增一条用户参与动态的记录。
      entity = new Entity("feed_user");
      entity.set("feedId", feedId).set("userId", user.getId());
      entity.set("status", 1).set("createTime", time);
      entityDao.save(entity);

      condition = new HashMap<String, Object>();
      condition.put("feedId", feedId);
      condition.put("status", 1);
      int userCount = entityDao.count("feed_user", condition);
      entityDao.update("feed", "id", feedId, "userCount", userCount);
    }

    long fromUserId = user.getId();
    String fromUsername = user.getUsername();
    // New comment notification.
    entity = new Entity("notification_comment");
    entity.set("fromUserId", fromUserId).set("fromUsername", fromUsername);
    entity.set("dataId", comment.getId()).set("content", content).set("feedId", feedId).set("feed", feed.getTitle());
    entity.set("status", 0).set("createTime", time);

    // 评论了...
    long toUserId = feed.getUserId();
    if (fromUserId != toUserId) {
      entity.set("type", 1).set("toUserId", toUserId);
      entityDao.save(entity);
    }

    // 回复了...
    if (replyUserId > 0 && replyUserId != toUserId) {
      toUserId = replyUserId;
      if (fromUserId != toUserId) {
        entity.set("type", 2).set("toUserId", replyUserId);
        entityDao.save(entity);
      }
    }

    LOG.info("New comment submitted: " + comment.getId());
    return ResponseBuilder.ok(comment.getId());
  }

  @GET
  @Path("{commentId}/delete")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> delete(@PathParam("commentId") long commentId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Comment comment = entityDao.get("comment", commentId, CommentRowMapper.getInstance());
    if (comment == null) {
      return ResponseBuilder.error(40404, "未找到要删除的评论。");
    }
    if (comment.getUserId() != user.getId()) {
      return ResponseBuilder.ERR_PERMISSION_DENIED;
    }
    entityDao.update("comment", "id", commentId, "status", Constants.STATUS_DELETED_BY_USER);

    // TODO: 修改成异步更新数量数据。
    long feedId = comment.getFeedId();
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("feedId", feedId);
    condition.put("commentId", commentId);
    if (entityDao.exists("feed_comment", condition)) {
      entityDao.update("feed_comment", condition, "status", Constants.STATUS_DELETED_BY_USER);

      condition = new HashMap<String, Object>();
      condition.put("feedId", feedId);
      condition.put("status", Constants.STATUS_YES);
      int count = entityDao.count("feed_comment", condition);
      entityDao.update("feed", "id", feedId, "commentCount", count);
    }

    return ResponseBuilder.ok(comment);
  }

  /**
   * 填充Feed 用户信息。
   */
  private void setCommentUserProperties(Comment comment) {
    if (comment == null) {
      return;
    }

    User user = entityDao.get("user", comment.getUserId(), UserRowMapper.getInstance());
    if (user != null) {
      comment.getProperties().put("username", user.getUsername());
      if (StringUtils.isNotEmpty(user.getAvatar())) {
        comment.getProperties().put("userAvatar", user.getAvatar());

      } else {
        comment.getProperties().put("userAvatar", "static/dabllo.png");
      }
    }

    User replyUser = entityDao.get("user", comment.getReplyUserId(), UserRowMapper.getInstance());
    if (replyUser != null) {
      comment.getProperties().put("replyUsername", replyUser.getUsername());
    }
  }

}
