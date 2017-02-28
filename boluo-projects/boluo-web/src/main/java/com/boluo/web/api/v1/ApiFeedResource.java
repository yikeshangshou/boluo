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
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.boluo.Constants;
import com.boluo.dao.mapper.FeedRowMapper;
import com.boluo.dao.mapper.TopicRowMapper;
import com.boluo.dao.mapper.UserRowMapper;
import com.boluo.model.Entity;
import com.boluo.model.Feed;
import com.boluo.model.Topic;
import com.boluo.model.User;
import com.boluo.service.TopicService;
import com.boluo.util.PageNumberUtils;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Dec 28, 2015
 */
@Path("/api/v1/feed")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiFeedResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiFeedResource.class);
  @Resource
  protected TopicService topicService;

  @GET
  @Path("{feedId}/delete")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> delete(@PathParam("feedId") long feedId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Feed feed = entityDao.get("feed", feedId, FeedRowMapper.getInstance());
    if (feed == null) {
      return ResponseBuilder.error(40404, "未找到要删除的动态。");
    }
    if (feed.getUserId() != user.getId()) {
      return ResponseBuilder.ERR_PERMISSION_DENIED;
    }
    entityDao.update("feed", "id", feedId, "status", Constants.STATUS_DELETED_BY_USER);

    // TODO: 修改成异步更新数量数据。
    long topicId = feed.getTopicId();
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("topicId", topicId);
    condition.put("feedId", feedId);
    if (entityDao.exists("topic_feed", condition)) {
      entityDao.update("topic_feed", condition, "status", Constants.STATUS_DELETED_BY_USER);

      condition = new HashMap<String, Object>();
      condition.put("topicId", topicId);
      condition.put("status", Constants.STATUS_YES);
      int count = entityDao.count("topic_feed", condition);
      entityDao.update("topic", "id", topicId, "feedCount", count);
    }

    return ResponseBuilder.ok(feed);
  }

  @GET
  @Path("{feedId}")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> get(@PathParam("feedId") long feedId) {
    Feed feed = entityDao.get("feed", feedId, FeedRowMapper.getInstance());
    if (feed != null) {
      setFeedImages(feed);
      setFeedUserProperties(feed);
    }

    return ResponseBuilder.ok(feed);
  }

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getFeeds(@QueryParam("topicId") long topicId, @QueryParam("userId") long userId, @QueryParam("source") String source,
      @QueryParam("offset") @DefaultValue("0") long offset) {
    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    Map<String, Object> condition = new HashMap<String, Object>();
    if (topicId > 0) {
      condition.put("topicId", topicId);
    }
    if (userId > 0) {
      condition.put("userId", userId);
    }
    if (StringUtils.isNotEmpty(source)) {
      condition.put("source", source);
    }
    condition.put("status", 1);
    List<Feed> feeds = entityDao.findByOffset("feed", condition, offset, PageNumberUtils.PAGE_SIZE_SMALL, FeedRowMapper.getInstance());
    if (CollectionUtils.isEmpty(feeds)) {
      // 没有查询到数据，直接返回。
      return ResponseBuilder.ok(null, -1);
    }

    for (Feed feed : feeds) {
      setFeedImages(feed);
      setFeedUserProperties(feed);
      if (userId > 0) {
        setFeedTopicProperties(feed);
      }

      // 更新offset：取最小的一个 id 为下一次查询的 offset。
      long id = feed.getId();
      offset = offset > id ? id : offset;
    }

    return ResponseBuilder.ok(feeds, offset);
  }

  @GET
  @Path("full_data")
  @Produces(APPLICATION_JSON)
  @SuppressWarnings("unchecked")
  public Map<String, Object> getFeedsFullData(@QueryParam("topicId") long topicId, @QueryParam("userId") long userId, @QueryParam("source") String source,
      @QueryParam("offset") @DefaultValue("0") long offset) {
    Map<String, Object> object = getFeeds(topicId, userId, source, offset);

    User user = getSessionUser();
    if (user == null) {
      return object;
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("userId", user.getId());
    List<Feed> feeds = (List<Feed>) object.get("r");
    if (CollectionUtils.isNotEmpty(feeds)) {
      for (Feed feed : feeds) {
        condition.put("feedId", feed.getId());
        Entity entity = entityDao.findOne("feed_like", condition);
        if (entity != null && entity.getInt("status") == Constants.STATUS_OK) {
          feed.setLikeStatus(Constants.STATUS_OK);
        }
      }
    }

    return object;
  }

  @GET
  @Path("{feedId}/like_status")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getLikeStatus(@PathParam("feedId") long feedId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("feedId", feedId);
    condition.put("userId", user.getId());
    Entity entity = entityDao.findOne("feed_like", condition);
    if (entity == null || entity.getInt("status") < 1) {
      return ResponseBuilder.ok(0);

    } else {
      return ResponseBuilder.ok(1);
    }
  }

  @GET
  @Path("mine")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getMyFeeds(@QueryParam("offset") @DefaultValue("0") long offset) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("userId", user.getId());
    condition.put("status", 1);
    List<Feed> feeds = entityDao.findByOffset("feed", condition, offset, PageNumberUtils.PAGE_SIZE_SMALL, FeedRowMapper.getInstance());
    if (CollectionUtils.isEmpty(feeds)) {
      // 没有查询到数据，直接返回。
      return ResponseBuilder.ok(null, -1);
    }

    for (Feed feed : feeds) {
      setFeedImages(feed);
      setFeedUserProperties(feed);
      setFeedTopicProperties(feed);

      // 更新offset：取最小的一个 id 为下一次查询的 offset。
      long id = feed.getId();
      offset = offset > id ? id : offset;
    }

    return ResponseBuilder.ok(feeds, offset);
  }

  @GET
  @Path("{feedId}/like")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> like(@PathParam("feedId") long feedId, @QueryParam("value") @DefaultValue("1") int value) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Feed feed = entityDao.get("feed", feedId, FeedRowMapper.getInstance());
    if (feed == null) {
      return ResponseBuilder.error(40404, "未找到动态。");
    }

    int status = 0;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("feedId", feedId);
    condition.put("userId", user.getId());
    if (entityDao.exists("feed_like", condition)) {
      status = value == 1 ? 1 : 0;
      entityDao.update("feed_like", condition, "status", status);

    } else {
      long time = System.currentTimeMillis();
      Entity entity = new Entity("feed_like");
      entity.set("userId", user.getId()).set("feedId", feedId);
      entity.set("status", 1).set("createTime", time).set("updateTime", time);
      status = 1;
      entityDao.save(entity);
    }

    // TODO: 修改成异步更新数量数据。
    condition = new HashMap<String, Object>();
    condition.put("feedId", feedId);
    condition.put("status", 1);
    int count = entityDao.count("feed_like", condition);
    entityDao.update("feed", "id", feedId, "likeCount", count);

    // TODO: New like notification.

    return ResponseBuilder.ok(status);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@FormParam("topicId") long topicId, @FormParam("source") String source, @FormParam("title") String title, @FormParam("description") String description,
      @FormParam("link") String link, @FormParam("image") String image, @FormParam("place") String place, @FormParam("longitude") String longitude, @FormParam("latitude") String latitude) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (StringUtils.isEmpty(title)) {
      return ResponseBuilder.error(40101, "请输入动态内容。");
    }
    if (StringUtils.length(title) > 200) {
      return ResponseBuilder.error(40102, "动态内容不能超过200个字。");
    }
    if (StringUtils.length(description) > 400) {
      return ResponseBuilder.error(40103, "动态描述不能超过400个字。");
    }
    if (StringUtils.isNotEmpty(link) && StringUtils.length(link) > 400) {
      return ResponseBuilder.error(40104, "链接不能超过400个字符。");
    }
    String[] images = StringUtils.split(image, ",");
    if (ArrayUtils.getLength(images) > 6) {
      return ResponseBuilder.error(40107, "动态图片不能超过6张。");
    }

    long time = System.currentTimeMillis();
    // Save feed.
    Entity feed = new Entity("feed");
    feed.set("userId", user.getId());
    feed.set("topicId", topicId).set("title", title);
    if (StringUtils.isNotEmpty(source)) { // 信息来源
      feed.set("source", source);
    } else {
      feed.set("source", "User");
    }
    if (StringUtils.isNotEmpty(link)) {
      feed.set("link", link);
    }
    if (ArrayUtils.isNotEmpty(images)) {
      feed.set("image", images[0]);
    }
    feed.set("place", place).set("longitude", longitude).set("latitude", latitude);
    feed.set("hidden", 0).set("status", 1).set("createTime", time);
    feed = entityDao.saveAndReturn(feed);

    // Save images.
    long feedId = feed.getId();
    if (ArrayUtils.isNotEmpty(images)) {
      for (String imagePath : images) {
        if (StringUtils.isBlank(imagePath)) {
          continue;
        }

        Entity feedImage = new Entity("feed_image");
        feedImage.set("feedId", feedId).set("path", imagePath);
        feedImage.set("status", 1).set("createTime", time);
        entityDao.save(feedImage);
      }
    }

    LOG.info("New feed submitted: " + feedId);
    return ResponseBuilder.ok(feedId);
  }

  /**
   * 填充Feed 图片信息。
   */
  private void setFeedImages(Feed feed) {
    if (feed == null) {
      return;
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("feedId", feed.getId());
    condition.put("status", 1);

    List<Entity> entities = entityDao.find("feed_image", condition, 1, 100);
    if (CollectionUtils.isNotEmpty(entities)) {
      for (Entity entity : entities) {
        feed.getImages().add(entity.getString("path"));
      }
    }
  }

  /**
   * 填充Feed 主题信息。
   */
  private void setFeedTopicProperties(Feed feed) {
    if (feed == null) {
      return;
    }

    Topic topic = entityDao.get("topic", feed.getTopicId(), TopicRowMapper.getInstance());
    if (topic == null) {
      return;
    }

    feed.getProperties().put("topicId", topic.getId());
    feed.getProperties().put("topicTitle", topic.getTitle());
  }

  /**
   * 填充Feed 用户信息。
   */
  private void setFeedUserProperties(Feed feed) {
    if (feed == null) {
      return;
    }

    User user = entityDao.get("user", feed.getUserId(), UserRowMapper.getInstance());
    if (user == null) {
      return;
    }

    feed.setUsername(user.getUsername());
    feed.getProperties().put("username", user.getUsername());
    if (StringUtils.isNotEmpty(user.getAvatar())) {
      feed.getProperties().put("userAvatar", user.getAvatar());

    } else {
      feed.getProperties().put("userAvatar", "static/dabllo.png");
    }
  }

}
