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
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.boluo.Constants;
import com.boluo.dao.mapper.ItemRowMapper;
import com.boluo.dao.mapper.TopicRowMapper;
import com.boluo.model.Entity;
import com.boluo.model.Item;
import com.boluo.model.Topic;
import com.boluo.model.User;
import com.boluo.service.ItemService;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Jul 6, 2016
 */
@Path("/api/v1/item")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiItemResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiItemResource.class);

  @Resource
  protected ItemService itemService;

  @GET
  @Path("{id}")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> get(@PathParam("id") long itemId) {
    if (itemId <= 0) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }
    Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
    if (item == null || item.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }

    setItemImages(item);
    setItemTopics(item);
    return ResponseBuilder.ok(item);
  }

  @GET
  @Path("{id}/topics")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getItemTopics(@PathParam("id") long itemId) {
    if (itemId <= 0) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("itemId", itemId);
    condition.put("status", Constants.STATUS_ENABLED);
    List<Entity> entities = entityDao.find("topic_item", condition, 1, 10);
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

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> get(@QueryParam("time") String time, @QueryParam("section") String section, @QueryParam("page") @DefaultValue("1") int page) {
    page = page > 0 ? page : 1;

    List<Item> items = null;
    if (StringUtils.isNotEmpty(section)) {
      items = itemService.getItemsBySection(section, page);

    } else {
      items = itemService.getItems(time, page);
    }

    return ResponseBuilder.ok(items);
  }

  @GET
  @Path("{id}/not_news")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> notNews(@PathParam("id") long itemId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (itemId <= 0) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }
    Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
    if (item == null || item.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }

    itemService.reportItem(user.getId(), itemId, 1);

    return ResponseBuilder.OK;
  }

  @GET
  @Path("{id}/old_news")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> oldNews(@PathParam("id") long itemId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (itemId <= 0) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }
    Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
    if (item == null || item.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }

    itemService.reportItem(user.getId(), itemId, 2);

    return ResponseBuilder.OK;
  }

  @GET
  @Path("{id}/read")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> read(@PathParam("id") long itemId, @QueryParam("source") @DefaultValue("1") int source, @QueryParam("value") @DefaultValue("1") int value) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (itemId <= 0) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }
    Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
    if (item == null || item.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }

    value = (value == 1) ? 1 : 0;
    itemService.readItem(user.getId(), itemId, source, value);
    return ResponseBuilder.OK;
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@FormParam("id") long id, @FormParam("source") String source, @FormParam("title") String title, @FormParam("description") String description,
      @FormParam("link") String link, @FormParam("image") String image) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (StringUtils.isEmpty(title)) {
      return ResponseBuilder.error(70101, "资讯标题不能为空。");
    }
    if (StringUtils.length(title) < 5) {
      return ResponseBuilder.error(70102, "资讯标题不能少于5个字。");
    }
    if (StringUtils.length(title) > 32) {
      return ResponseBuilder.error(70103, "资讯标题不能超过32个字。");
    }
    if (StringUtils.length(description) > 400) {
      return ResponseBuilder.error(70104, "资讯描述不能超过400个字。");
    }
    if (StringUtils.isEmpty(link)) {
      return ResponseBuilder.error(70105, "资讯链接不能为空。");
    }
    if (!StringUtils.startsWith(link, "http://") && !StringUtils.startsWith(link, "https://")) {
      return ResponseBuilder.error(70106, "资讯链接不是一个合法的网页链接。");
    }
    if (StringUtils.length(link) > 400) {
      return ResponseBuilder.error(70107, "资讯链接不能超过400个字符。");
    }

    String[] images = StringUtils.split(image, ",");
    if (ArrayUtils.getLength(images) > 6) {
      return ResponseBuilder.error(70108, "资讯图片不能超过6张。");
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("link", link);
    if (entityDao.exists("item", condition)) {
      return ResponseBuilder.error(70405, "已经发布过的资讯。");
    }

    long time = System.currentTimeMillis();
    // Save item.
    if (id > 0) {
      // 编辑结果保存到草稿，审核通过后进行替换。
      Entity item = new Entity("item_draft");
      item.set("relatedId", id).set("userId", user.getId());
      item.set("title", title).set("description", description).set("link", link);
      if (ArrayUtils.isNotEmpty(images)) {
        item.set("image", images[0]);
      }
      item.set("rank", 0).set("publishTime", time).set("status", 1).set("createTime", time);
      item = entityDao.saveAndReturn(item);
      return ResponseBuilder.OK;

    } else {
      // 投稿发送到feed表，审核通过后发布到item。
      Entity feed = new Entity("feed");
      feed.set("userId", user.getId());
      if (StringUtils.isNotEmpty(source)) { // 信息来源
        feed.set("source", source);
      } else {
        feed.set("source", "User");
      }
      feed.set("title", title).set("description", description).set("link", link);
      if (ArrayUtils.isNotEmpty(images)) {
        feed.set("image", images[0]);
      }
      feed.set("status", 1).set("createTime", time);
      feed = entityDao.saveAndReturn(feed);

      // Save images.
      long feedId = feed.getId();
      if (ArrayUtils.isNotEmpty(images)) {
        for (String imagePath : images) {
          if (StringUtils.isBlank(imagePath)) {
            continue;
          }

          Entity itemImage = new Entity("feed_image");
          itemImage.set("feedId", feedId).set("path", imagePath);
          itemImage.set("status", 1).set("createTime", time);
          entityDao.save(itemImage);
        }
      }

      LOG.info("New item submitted: " + feedId);
      return ResponseBuilder.ok(feedId);
    }
  }

  @GET
  @Path("{id}/up")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> up(@PathParam("id") long itemId, @QueryParam("source") @DefaultValue("1") int source, @QueryParam("value") @DefaultValue("1") int value) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (itemId <= 0) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }
    Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
    if (item == null || item.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }

    long time = System.currentTimeMillis();
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("itemId", itemId);
    condition.put("userId", user.getId());
    if (entityDao.exists("item_up", condition)) {
      value = (value == 1) ? 1 : 0;

      Map<String, Object> updateValues = new HashMap<String, Object>();
      updateValues.put("source", source);
      updateValues.put("value", value);
      updateValues.put("updateTime", time);
      entityDao.update("item_up", condition, updateValues);

    } else {
      Entity entity = new Entity("item_up");
      entity.set("itemId", itemId).set("userId", user.getId());
      entity.set("source", source).set("value", value).set("time", time);
      entity.set("status", 1).set("createTime", time).set("updateTime", time);
      entityDao.save(entity);
    }

    return ResponseBuilder.ok(value);
  }

  @GET
  @Path("{id}/up_status")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> upStatus(@PathParam("id") long itemId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("itemId", itemId);
    condition.put("userId", user.getId());
    Entity entity = entityDao.findOne("item_up", condition);
    if (entity != null && entity.getInt("value") == 1) {
      return ResponseBuilder.ok(1);

    } else {
      return ResponseBuilder.ok(0);
    }
  }

  @GET
  @Path("{id}/delete")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> delete(@PathParam("id") long itemId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (itemId <= 0) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }
    Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
    if (item == null || item.getStatus() < Constants.STATUS_ENABLED) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }
    if (item.getUserId() != user.getId()) {
      return ResponseBuilder.ERR_PERMISSION_DENIED;
    }

    entityDao.update("item", "id", itemId, "status", Constants.STATUS_DELETED_BY_USER);
    return ResponseBuilder.OK;
  }

  /**
   * 填充Item 图片信息。
   */
  private void setItemImages(Item item) {
    if (item == null) {
      return;
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("itemId", item.getId());
    condition.put("status", 1);
    List<Entity> entities = entityDao.find("item_image", condition, 1, 10);
    if (CollectionUtils.isNotEmpty(entities)) {
      for (Entity entity : entities) {
        item.getImages().add(entity.getString("path"));
      }
    }
  }

  /**
   * 填充Item 专题标签。
   */
  private void setItemTopics(Item item) {
    if (item == null) {
      return;
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("itemId", item.getId());
    condition.put("status", 1);
    List<Entity> entities = entityDao.find("topic_item", condition, 1, 5);

    if (CollectionUtils.isNotEmpty(entities)) {
      List<Map<String, Object>> topics = new ArrayList<Map<String, Object>>();
      for (Entity entity : entities) {
        Topic topic = entityDao.get("topic", entity.getLong("topicId"), TopicRowMapper.getInstance());
        if (topic != null & topic.isEnabled()) {
          Map<String, Object> map = new HashMap<String, Object>();
          map.put("topicId", topic.getId());
          map.put("topicName", topic.getName());
          map.put("topicTitle", topic.getTitle());
          topics.add(map);
        }
      }

      item.getProperties().put("topics", topics);
    }
  }

}
