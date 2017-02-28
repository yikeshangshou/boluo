package com.boluo.web;

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

import com.boluo.Constants;
import com.boluo.dao.mapper.DiscussionRowMapper;
import com.boluo.dao.mapper.TopicRowMapper;
import com.boluo.model.Article;
import com.boluo.model.Discussion;
import com.boluo.model.Entity;
import com.boluo.model.Item;
import com.boluo.model.Topic;
import com.boluo.service.ArticleService;
import com.boluo.service.ItemService;
import com.boluo.util.Pair;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Feb 22, 2016
 */
@Path("/topic")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class TopicResource extends BaseResource {

  @Resource
  protected ItemService itemService;
  @Resource
  protected ArticleService articleService;

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index() {
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("status", Constants.STATUS_ENABLED);
    List<Topic> topics = entityDao.find("topic", condition, 1, 100, TopicRowMapper.getInstance());
    request.setAttribute("topics", topics);

    return Response.ok(new Viewable("index")).build();
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.TEXT_HTML)
  public Response get(@PathParam("id") long topicId) {
    Topic topic = entityDao.get("topic", topicId, TopicRowMapper.getInstance());
    if (topic == null || !topic.isEnabled()) {
      request.setAttribute("_blank", true);
      request.setAttribute("_error", "未找到专题：专题可能被删除。");
      return Response.ok(new Viewable("topic")).build();
    }
    request.setAttribute("topic", topic);

    Pair<Long, List<Item>> items = itemService.getItemsByTopic(topicId, 0, 100);
    request.setAttribute("items", items.right);

    Pair<Long, List<Article>> articles = articleService.getArticlesByTopic(topicId, 0, 10);
    request.setAttribute("articles", articles.right);

    return Response.ok(new Viewable("topic")).build();
  }

  @GET
  @Path("{id}/plain")
  @Produces(MediaType.TEXT_HTML)
  public Response getPlainText(@PathParam("id") long topicId) {
    Topic topic = entityDao.get("topic", topicId, TopicRowMapper.getInstance());
    if (topic == null || !topic.isEnabled()) {
      request.setAttribute("_blank", true);
      request.setAttribute("_error", "未找到专题：专题可能被删除。");
      return Response.ok(new Viewable("topic")).build();
    }
    request.setAttribute("topic", topic);

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("topicId", topicId);
    condition.put("status", Constants.STATUS_ENABLED);
    long begin = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L;
    int itemCount = entityDao.countGreater("topic_item", condition, "createTime", begin);
    request.setAttribute("itemCount", itemCount);

    Pair<Long, List<Item>> items = itemService.getItemsByTopic(topicId, 0, 20);
    request.setAttribute("items", items.right);

    condition = new HashMap<String, Object>();
    condition.put("topicId", topicId);
    condition.put("status", Constants.STATUS_ENABLED);
    int articleCount = entityDao.countGreater("topic_article", condition, "createTime", begin);
    request.setAttribute("articleCount", articleCount);

    Pair<Long, List<Article>> articles = articleService.getArticlesByTopic(topicId, 0, 5);
    request.setAttribute("articles", articles.right);

    List<Entity> entities = entityDao.find("topic_item", condition, 1, 20);
    List<Discussion> discussions = entityDao.findOrIn("discussion", "topicId", topicId, "itemId", entities, Long.MAX_VALUE, 10, DiscussionRowMapper.getInstance());
    request.setAttribute("discussions", discussions);

    return Response.ok(new Viewable("plain")).build();
  }

}
