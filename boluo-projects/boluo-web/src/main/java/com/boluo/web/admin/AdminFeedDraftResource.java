package com.boluo.web.admin;

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

import com.boluo.dao.mapper.CategoryRowMapper;
import com.boluo.dao.mapper.FeedRowMapper;
import com.boluo.model.Category;
import com.boluo.model.Entity;
import com.boluo.model.Feed;
import com.boluo.util.PageNumberUtils;
import com.boluo.util.Pair;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Jul 5, 2016
 */
@Path("/admin/feed_draft")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AdminFeedDraftResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(AdminFeedDraftResource.class);

  @GET
  @Path("{id}/delete")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> delete(@PathParam("id") long id) {
    try {
      entityDao.delete("feed_draft", id);
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      return ResponseBuilder.error(1, "");
    }
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index(@QueryParam("source") String source, @QueryParam("page") int page) {
    page = page > 0 ? page : 1;

    Map<String, Object> condition = new HashMap<String, Object>();
    if (StringUtils.isNotEmpty(source)) {
      condition.put("source", source);
    }
    condition.put("status", 0);
    Pair<Integer, List<Feed>> result = entityDao.findAndCount("feed_draft", condition, page, PageNumberUtils.PAGE_SIZE_MEDIUM, FeedRowMapper.getInstance());
    request.setAttribute("feeds", result.right);

    int count = result.left;
    Pair<List<Integer>, Integer> pages = PageNumberUtils.generate(page, count, PageNumberUtils.PAGE_SIZE_MEDIUM);
    request.setAttribute("currentPage", page);
    request.setAttribute("pages", pages.left);
    request.setAttribute("lastPage", pages.right);

    condition = new HashMap<String, Object>();
    condition.put("status", 1);
    condition.put("parentId", 0);
    List<Category> categories = entityDao.find("category", condition, CategoryRowMapper.getInstance());
    request.setAttribute("categories", categories);
    return Response.ok(new Viewable("index")).build();
  }

  @POST
  @Path("publish")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> publish(@FormParam("categoryId") long categoryId, @FormParam("topicId") long topicId, @FormParam("feedDraftId") long feedDraftId, @FormParam("title") String title) {
    Feed feedDraft = entityDao.get("feed_draft", feedDraftId, FeedRowMapper.getInstance());

    if (StringUtils.length(title) > 200) {
      return ResponseBuilder.error(40102, "动态内容不能超过200个字。");
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("topicId", topicId);
    condition.put("link", feedDraft.getLink());
    if (entityDao.exists("feed", condition)) {
      return ResponseBuilder.error(50000, "该主题中已经发布了这条新闻资讯。");
    }

    long time = feedDraft.getCreateTime();
    Entity feed = new Entity("feed");
    feed.set("userId", feedDraft.getUserId());
    feed.set("topicId", topicId).set("title", title);
    feed.set("link", feedDraft.getLink());
    feed.set("image", feedDraft.getImage());
    feed.set("hidden", 0).set("status", 1).set("createTime", time);
    feed = entityDao.saveAndReturn(feed);

    long feedId = feed.getId();
    Entity feedImage = new Entity("feed_image");
    feedImage.set("feedId", feedId).set("path", feedDraft.getImage());
    feedImage.set("status", 1).set("createTime", time);
    entityDao.save(feedImage);

    Entity entity = new Entity("topic_feed");
    entity.set("topicId", topicId).set("feedId", feedId);
    entity.set("status", 1).set("createTime", time);
    entityDao.save(entity);

    condition = new HashMap<String, Object>();
    condition.put("topicId", topicId);
    condition.put("status", 1);
    int feedCount = entityDao.count("topic_feed", condition);
    entityDao.update("topic", "id", topicId, "feedCount", feedCount);

    // 更新状态为已经发布。
    LOG.info("Feed draft " + feedDraftId + " has been published into Topic " + topicId);
    entityDao.update("feed_draft", "id", feedDraftId, "status", 1);

    return ResponseBuilder.OK;
  }

}
