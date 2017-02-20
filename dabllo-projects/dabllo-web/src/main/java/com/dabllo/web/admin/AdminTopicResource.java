package com.dabllo.web.admin;

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
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.dao.mapper.TopicRowMapper;
import com.dabllo.model.Topic;
import com.dabllo.model.User;
import com.dabllo.util.ImageUtils;
import com.dabllo.util.PageNumberUtils;
import com.dabllo.util.Pair;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Mar 7, 2014
 */
@Path("/admin/topic")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AdminTopicResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(AdminTopicResource.class);

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index(@QueryParam("selected") int selected, @QueryParam("status") int status, @QueryParam("page") int page) {
    page = page > 0 ? page : 1;

    Map<String, Object> condition = new HashMap<String, Object>();
    if (selected != 0) {
      condition.put("selected", selected);
    }
    if (status != 0) {
      condition.put("status", status);
    }
    Pair<Integer, List<Topic>> result = entityDao.findAndCount("topic", condition, page, PageNumberUtils.PAGE_SIZE_LARGE, TopicRowMapper.getInstance());

    List<Topic> topics = result.right;
    request.setAttribute("selected", selected);
    request.setAttribute("status", status);
    request.setAttribute("topics", topics);

    int count = result.left;
    Pair<List<Integer>, Integer> pages = PageNumberUtils.generate(page, count, PageNumberUtils.PAGE_SIZE_LARGE);
    request.setAttribute("currentPage", page);
    request.setAttribute("pages", pages.left);
    request.setAttribute("lastPage", pages.right);

    return Response.ok(new Viewable("index")).build();
  }

  @POST
  @Path("update")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@FormParam("topicId") long topicId, @FormParam("title") String title, @FormParam("description") String description, @FormParam("link") String link,
      @FormParam("image") String image) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (StringUtils.isEmpty(title)) {
      return ResponseBuilder.error(30101, "专题标题不能为空。");
    }
    if (StringUtils.length(title) < 2) {
      return ResponseBuilder.error(30102, "专题标题不能少于2个字。");
    }
    if (StringUtils.length(title) > 32) {
      return ResponseBuilder.error(30103, "专题标题不能超过32个字符。");
    }
    if (StringUtils.length(description) > 400) {
      return ResponseBuilder.error(30104, "专题描述不能超过400个字符。");
    }
    if (StringUtils.isNotEmpty(link)) {
      if (!StringUtils.startsWith(link, "http://") && !StringUtils.startsWith(link, "https://")) {
        return ResponseBuilder.error(30106, "专题链接不是一个合法的网页链接。");
      }
      if (StringUtils.length(link) > 400) {
        return ResponseBuilder.error(30107, "专题链接不能超过400个字符。");
      }
    }

    Topic topic = entityDao.get("topic", topicId, TopicRowMapper.getInstance());
    if (topic == null) {
      return ResponseBuilder.error(30404, "未找到专题。");
    }

    long time = System.currentTimeMillis();
    Map<String, Object> updateValues = new HashMap<String, Object>();
    if (!StringUtils.equals(topic.getTitle(), title)) {
      updateValues.put("title", title);
    }
    if (!StringUtils.equals(topic.getDescription(), description)) {
      updateValues.put("description", description);
    }
    if (!StringUtils.equals(topic.getLink(), link)) {
      updateValues.put("link", link);
    }
    if (!StringUtils.equals(topic.getImage(), image)) {
      updateValues.put("image", image);
    }
    updateValues.put("updateTime", time);
    entityDao.update("topic", "id", topicId, updateValues);

    updateValues.put("id", topicId);
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
      entityDao.update("topic", "id", id, "status", value);
      LOG.info("Update topic status: " + value);
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.error("Failed to update topic status: " + id, t);
      return ResponseBuilder.error(50000, "Failed to update topic status: " + id);
    }
  }

}
