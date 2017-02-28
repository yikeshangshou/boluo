package com.boluo.web.api.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.boluo.Constants;
import com.boluo.dao.mapper.ItemRowMapper;
import com.boluo.dao.mapper.TopicRowMapper;
import com.boluo.model.Entity;
import com.boluo.model.Item;
import com.boluo.model.Topic;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;

/**
 * 
 * @author mixueqiang
 * @since Aug 20, 2016
 */
@Path("/api/v1/article")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiArticleResource extends BaseResource {

  @GET
  @Path("{id}")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> get(@PathParam("id") long itemId) {
    if (itemId <= 0) {
      return ResponseBuilder.error(75404, "未找到文章。");
    }
    Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
    if (item == null || !item.isEnabled()) {
      return ResponseBuilder.error(75404, "未找到文章。");
    }

    return ResponseBuilder.ok(item);
  }

  @GET
  @Path("{id}/topics")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getItemTopics(@PathParam("id") long articleId) {
    if (articleId <= 0) {
      return ResponseBuilder.error(75404, "未找到文章。");
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("articleId", articleId);
    condition.put("status", Constants.STATUS_ENABLED);
    List<Entity> entities = entityDao.find("topic_article", condition, 1, 10);
    if (CollectionUtils.isEmpty(entities)) {
      return ResponseBuilder.OK;
    }

    List<Topic> topics = new ArrayList<Topic>();
    for (Entity entity : entities) {
      Topic topic = entityDao.get("topic", entity.getLong("topicId"), TopicRowMapper.getInstance());
      if (topic != null) {
        topics.add(topic);
      }
    }

    return ResponseBuilder.ok(topics);
  }

}
