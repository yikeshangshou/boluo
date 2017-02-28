package com.boluo.web.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.boluo.Constants;
import com.boluo.dao.mapper.ItemRowMapper;
import com.boluo.dao.mapper.TopicRowMapper;
import com.boluo.model.Entity;
import com.boluo.model.Item;
import com.boluo.model.Topic;
import com.boluo.model.User;
import com.boluo.util.ImageUtils;
import com.boluo.util.PageNumberUtils;
import com.boluo.util.Pair;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Jul 12, 2016
 */
@Path("/admin/item")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AdminItemResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(AdminItemResource.class);

  @GET
  @Path("{id}/delete")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> delete(@PathParam("id") long id) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    try {
      entityDao.remove("section_16h", "itemId", id);
      entityDao.remove("section_24h", "itemId", id);
      entityDao.update("item", "id", id, "status", Constants.STATUS_DELETED_BY_ADMIN);

      LOG.info("Admin " + user.getId() + " removed item: " + id);
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.error("Failed to delete item: " + id, t);
      return ResponseBuilder.error(101, "Failed to delete item: " + id);
    }
  }

  @GET
  @Path("{id}/fire")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> fire(@PathParam("id") long id, @QueryParam("value") @DefaultValue("1") int value) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    value = value == 1 ? 1 : 0;
    try {
      entityDao.update("item", "id", id, "hot", value);

      LOG.info("Admin " + user.getId() + " fired item: " + id);
      return ResponseBuilder.ok(value);

    } catch (Throwable t) {
      LOG.error("Failed to fire feed: " + id, t);
      return ResponseBuilder.error(101, "Failed to fire feed: " + id);
    }
  }

  @POST
  @Path("add_topic")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> addTopic(@FormParam("itemId") long itemId, @FormParam("title") String title, @FormParam("push") int push) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    try {
      long time = System.currentTimeMillis();
      long topicId = 0;
      Topic topic = entityDao.findOne("topic", "name", title, TopicRowMapper.getInstance());
      if (topic == null) {
        Entity entity = new Entity("topic");
        entity.set("userId", user.getId()).set("name", title).set("title", title);
        entity.set("selected", Constants.STATUS_NO).set("status", Constants.STATUS_ENABLED).set("createTime", time);
        entity = entityDao.saveAndReturn(entity);
        topicId = entity.getId();

        topic = entityDao.get("topic", topicId, TopicRowMapper.getInstance());

      } else {
        topicId = topic.getId();
      }

      Entity entity = new Entity("topic_item");
      entity.set("topicId", topicId).set("itemId", itemId);
      entity.set("status", Constants.STATUS_ENABLED).set("createTime", time);
      entityDao.save(entity);

      // 更新专题的最后更新时间。
      entityDao.update("topic", "id", topicId, "lastUpdateTime", time);
      LOG.info("Admin " + user.getId() + " add item " + itemId + " into topic " + topicId);

      if (push == 1) {
        Item item = entityDao.findOne("item", "id", itemId, ItemRowMapper.getInstance());
        if (item != null) {
          String type = "topic";
          StringBuffer sb = new StringBuffer();
          sb.append("你关注的专题");
          sb.append(topic.getName());
          sb.append("有了新");
          sb.append("资讯 ：\"");
          sb.append(item.getTitle());
          sb.append("\"");

          Entity entityNotice = new Entity("notification_user_text");
          entityNotice.set("content", sb.toString()).set("objectId", topicId);
          entityNotice.set("status", Constants.STATUS_NO).set("createTime", time).set("type", type);
          entityDao.save(entityNotice);
        }
      }

      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.error("Failed to add item into topic: " + itemId, t);
      return ResponseBuilder.error(50000, "添加资讯到专题失败: " + itemId);
    }
  }

  @GET
  @Path("{remove_topic}")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> removeTopic(@QueryParam("itemId") long itemId, @QueryParam("topicId") long topicId) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("itemId", itemId);
    condition.put("topicId", topicId);
    entityDao.update("topic_item", condition, "status", Constants.STATUS_DISABLED);
    return ResponseBuilder.OK;
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.TEXT_HTML)
  public Response get(@PathParam("id") long id) {
    Item item = entityDao.get("item", id, ItemRowMapper.getInstance());
    if (item == null) {
      request.setAttribute("_blank", true);
      request.setAttribute("_error", "未找到资讯。");
    }

    request.setAttribute("item", item);
    return Response.ok(new Viewable("item")).build();
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index(@QueryParam("page") int page) {
    page = page > 0 ? page : 1;

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("status", Constants.STATUS_ENABLED);
    Pair<Integer, List<Item>> result = entityDao.findAndCount("item", condition, page, PageNumberUtils.PAGE_SIZE_MEDIUM, ItemRowMapper.getInstance());
    request.setAttribute("items", result.right);

    int count = result.left;
    Pair<List<Integer>, Integer> pages = PageNumberUtils.generate(page, count, PageNumberUtils.PAGE_SIZE_MEDIUM);
    request.setAttribute("currentPage", page);
    request.setAttribute("pages", pages.left);
    request.setAttribute("lastPage", pages.right);

    return Response.ok(new Viewable("index")).build();
  }

  @POST
  @Path("update")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  @Transactional()
  public Map<String, Object> update(@FormParam("itemId") long itemId, @FormParam("title") String title, @FormParam("description") String description, @FormParam("link") String link,
      @FormParam("image") String image) {
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

    Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
    if (item == null) {
      return ResponseBuilder.error(70404, "未找到资讯。");
    }

    long time = System.currentTimeMillis();
    Map<String, Object> updateValues = new HashMap<String, Object>();
    if (!StringUtils.equals(item.getTitle(), title)) {
      updateValues.put("title", title);
    }
    if (!StringUtils.equals(item.getDescription(), description)) {
      updateValues.put("description", description);
    }
    if (!StringUtils.equals(item.getLink(), link)) {
      updateValues.put("link", link);
    }
    if (!StringUtils.equals(item.getImage(), image)) {
      updateValues.put("image", image);
    }
    updateValues.put("updateTime", time);
    entityDao.update("item", "id", itemId, updateValues);
    entityDao.update("item_image", "itemId", itemId, "status", Constants.STATUS_DELETED_BY_ADMIN);

    Entity itmeImage = new Entity("item_image");
    itmeImage.set("itemId", itemId).set("path", image);
    itmeImage.set("status", 1).set("createTime", time);
    entityDao.save(itmeImage);

    updateValues.put("id", itemId);
    updateValues.put("imageUrl", ImageUtils.getImageUrl(image));
    return ResponseBuilder.ok(updateValues);
  }

  @GET
  @Path("{id}/update_status")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> updateStatus(@PathParam("id") long id, @QueryParam("value") int value) {
    if (value != 1 && value != 0 && value != -3) {
      return ResponseBuilder.error(50000, "不允许的状态更新!");
    }

    try {
      entityDao.remove("section_16h", "itemId", id);
      entityDao.remove("section_24h", "itemId", id);
      entityDao.update("item", "id", id, "status", value);
      LOG.info("Admin " + getSessionUserId() + " updated item status: " + value);
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.error("Failed to updated item status: " + id, t);
      return ResponseBuilder.error(50000, "Failed to updated item status: " + id);
    }
  }

}
