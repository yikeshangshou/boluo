package com.boluo.web.api.v1;

import java.util.ArrayList;
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
import com.boluo.dao.BaseDao;
import com.boluo.dao.mapper.ArticleRowMapper;
import com.boluo.dao.mapper.ItemRowMapper;
import com.boluo.dao.mapper.TopicRowMapper;
import com.boluo.model.Article;
import com.boluo.model.Discussion;
import com.boluo.model.Entity;
import com.boluo.model.Item;
import com.boluo.model.Topic;
import com.boluo.model.User;
import com.boluo.service.ArticleService;
import com.boluo.service.DiscussionService;
import com.boluo.service.ItemService;
import com.boluo.service.TopicService;
import com.boluo.util.PageNumberUtils;
import com.boluo.util.Pair;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Apr 29, 2016
 */
@Path("/api/v1/topic")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiTopicResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiTopicResource.class);

  @Resource
  protected TopicService topicService;
  @Resource
  protected ItemService itemService;
  @Resource
  protected ArticleService articleService;
  @Resource
  protected DiscussionService discussionService;

  @GET
  @Path("{id}/follow")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> follow(@PathParam("id") long topicId, @QueryParam("value") @DefaultValue("1") int value) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Topic topic = entityDao.get("topic", topicId, TopicRowMapper.getInstance());
    if (topic == null || !topic.isEnabled()) {
      return ResponseBuilder.error(30404, "未找到专题。");
    }

    int status = topicService.followTopic(user.getId(), topicId, value);
    return ResponseBuilder.ok(status);
  }

  @GET
  @Path("{id}")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> get(@PathParam("id") long topicId) {
    Topic topic = entityDao.get("topic", topicId, TopicRowMapper.getInstance());
    if (topic == null || !topic.isEnabled()) {
      return ResponseBuilder.error(30404, "未找到专题。");
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("topicId", topicId);
    condition.put("status", Constants.STATUS_ENABLED);
    List<Entity> entities = entityDao.find("topic_item", condition, 1, PageNumberUtils.PAGE_SIZE_SMALL, "createTime", BaseDao.ORDER_OPTION_DESC);
    List<Item> items = new ArrayList<Item>();
    if (CollectionUtils.isNotEmpty(entities)) {
      for (Entity entity : entities) {
        Item item = entityDao.get("item", entity.getLong("itemId"), ItemRowMapper.getInstance());
        if (item != null && item.isEnabled()) {
          items.add(item);
        }
      }
    }
    topic.getProperties().put("items", items);

    entities = entityDao.find("topic_article", condition, 1, PageNumberUtils.PAGE_SIZE_SMALL, "createTime", BaseDao.ORDER_OPTION_DESC);
    List<Article> articles = new ArrayList<Article>();
    if (CollectionUtils.isNotEmpty(entities)) {
      for (Entity entity : entities) {
        Article article = entityDao.get("article", entity.getLong("articleId"), ArticleRowMapper.getInstance());
        if (article != null && article.isEnabled()) {
          articles.add(article);
        }
      }
    }
    topic.getProperties().put("articles", articles);

    return ResponseBuilder.ok(topic);
  }

  @GET
  @Path("{id}/following_status")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getFollowingStatus(@PathParam("id") long topicId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("topicId", topicId);
    condition.put("userId", user.getId());
    Entity entity = entityDao.findOne("topic_follow", condition);
    if (entity == null || entity.getInt("status") < 1) {
      return ResponseBuilder.ok(0);

    } else {
      return ResponseBuilder.ok(1);
    }
  }

  @GET
  @Path("following")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getFollowingTopics(@QueryParam("offset") @DefaultValue("0") long offset) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    Pair<Long, List<Topic>> pair = topicService.getFollowingTopics(user.getId(), offset);
    List<Topic> topics = pair.right;
    if (CollectionUtils.isNotEmpty(topics)) {
      for (Topic topic : topics) {

        // 未读小红点属性。
        setUnreadProperty(topic, user.getId());
      }
    }
    return ResponseBuilder.ok(pair.right, pair.left);
  }

  @GET
  @Path("{id}/items")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getTopicItems(@PathParam("id") long topicId, @QueryParam("offset") @DefaultValue("0") long offset) {
    offset = offset > 0 ? offset : Long.MAX_VALUE;
    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    Pair<Long, List<Item>> pair = itemService.getItemsByTopic(topicId, offset, 10);
    List<Item> items = pair.right;
    if (CollectionUtils.isNotEmpty(items)) {
      for (Item item : items) {
      }
    }

    return ResponseBuilder.ok(pair.right, pair.left);
  }

  @GET
  @Path("{id}/articles")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getTopicArticles(@PathParam("id") long topicId, @QueryParam("offset") @DefaultValue("0") long offset) {
    offset = offset > 0 ? offset : Long.MAX_VALUE;
    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    Pair<Long, List<Article>> pair = articleService.getArticlesByTopic(topicId, offset, 10);
    List<Article> articles = pair.right;
    if (CollectionUtils.isNotEmpty(articles)) {
      for (Article article : articles) {
      }
    }

    return ResponseBuilder.ok(pair.right, pair.left);
  }

  @GET
  @Path("{id}/discussions")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getTopicDiscussions(@PathParam("id") long topicId, @QueryParam("offset") @DefaultValue("0") long offset) {
    offset = offset > 0 ? offset : Long.MAX_VALUE;
    // 已经没有下一页数据。
    if (offset < 0) {
      return ResponseBuilder.ok(null, -1);
    }

    Pair<Long, List<Discussion>> pair = discussionService.getDiscussionsByTopic(topicId, offset);
    List<Discussion> discussions = pair.right;
    if (CollectionUtils.isNotEmpty(discussions)) {
      for (Discussion discussion : discussions) {
        setTopicOrItemProperties(discussion);
        setDiscussionProperties(discussion, getSessionUserId());
      }
    }

    return ResponseBuilder.ok(pair.right, pair.left);
  }

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getTopics(@QueryParam("offset") @DefaultValue("0") long offset, @QueryParam("size") @DefaultValue("10") int size) {
    offset = offset > 0 ? offset : Long.MAX_VALUE;
    size = (size > 0 && size < 50) ? size : PageNumberUtils.PAGE_SIZE_SMALL;

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("status", Constants.STATUS_ENABLED);
    List<Topic> topics = entityDao.findByOffset("topic", condition, offset, size, TopicRowMapper.getInstance());
    for (Topic topic : topics) {
      // 更新offset
      long id = topic.getId();
      offset = offset > id ? id : offset;
    }

    return ResponseBuilder.ok(topics, offset);
  }

  @GET
  @Path("{id}/read")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> read(@PathParam("id") long topicId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (topicId <= 0) {
      return ResponseBuilder.error(30404, "未找到讨论。");
    }
    Topic topic = entityDao.get("topic", topicId, TopicRowMapper.getInstance());
    if (topic == null) {
      return ResponseBuilder.error(30404, "未找到讨论。");
    }

    topicService.readTopic(user.getId(), topicId);
    return ResponseBuilder.OK;
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@FormParam("title") String title, @FormParam("description") String description, @FormParam("image") String image) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (StringUtils.isEmpty(title)) {
      return ResponseBuilder.error(30101, "请输入主题标题。");
    }
    if (StringUtils.length(title) > 100) {
      return ResponseBuilder.error(30102, "主题标题不能超过100个字符。");
    }
    if (StringUtils.length(description) > 100) {
      return ResponseBuilder.error(30103, "主题描述不能超过200个字符。");
    }

    long time = System.currentTimeMillis();
    // Save the topic.
    Entity topic = new Entity("topic");
    topic.set("userId", user.getId()).set("name", title);
    topic.set("title", title).set("description", description).set("image", image);
    topic.set("hot", Constants.STATUS_NO).set("top", Constants.STATUS_NO);
    topic.set("status", Constants.STATUS_ENABLED).set("createTime", time);
    topic = entityDao.saveAndReturn(topic);

    // Follow the topic.
    topicService.followTopic(user.getId(), topic.getId(), 1);

    LOG.info("New topic submitted: " + title);
    return ResponseBuilder.ok(topic.getId());
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

  private void setUnreadProperty(Topic topic, long userId) {
    if (topic.getLastUpdateTime() == 0) {
      topic.getProperties().put("unread", Constants.STATUS_NO);
      return;
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("userId", userId);
    condition.put("topicId", topic.getId());
    Entity entity = entityDao.findOne("topic_read", condition);

    if (entity == null || entity.getLong("updateTime") < topic.getLastUpdateTime()) {
      topic.getProperties().put("unread", Constants.STATUS_YES);

    } else {
      topic.getProperties().put("unread", Constants.STATUS_NO);
    }

  }

}
