package com.dabllo.web.admin;

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

import com.dabllo.Constants;
import com.dabllo.dao.mapper.FeedRowMapper;
import com.dabllo.model.Entity;
import com.dabllo.model.Feed;
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
@Path("/admin/feed")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AdminFeedResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(AdminFeedResource.class);

  @GET
  @Path("{id}/delete")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> delete(@PathParam("id") long id) {
    try {
      entityDao.delete("feed", id);
      return ResponseBuilder.OK;

    } catch (Throwable t) {
      LOG.error("Failed to delete feed: " + id, t);
      return ResponseBuilder.error(101, "Failed to delete feed: " + id);
    }
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getFeeds(@QueryParam("source") String source, @QueryParam("page") int page) {
    page = page > 0 ? page : 1;

    Map<String, Object> condition = new HashMap<String, Object>();
    if (StringUtils.isNotEmpty(source)) {
      condition.put("source", source);
    }
    condition.put("status", 1);
    Pair<Integer, List<Feed>> result = entityDao.findAndCount("feed", condition, page, PageNumberUtils.PAGE_SIZE_MEDIUM, FeedRowMapper.getInstance());
    request.setAttribute("feeds", result.right);

    int count = result.left;
    Pair<List<Integer>, Integer> pages = PageNumberUtils.generate(page, count, PageNumberUtils.PAGE_SIZE_MEDIUM);
    request.setAttribute("currentPage", page);
    request.setAttribute("pages", pages.left);
    request.setAttribute("lastPage", pages.right);

    return Response.ok(new Viewable("feeds")).build();
  }

  @GET
  @Path("user")
  @Produces(MediaType.TEXT_HTML)
  public Response getUserFeeds(@QueryParam("userId") long userId, @QueryParam("page") int page) {
    page = page > 0 ? page : 1;

    Pair<Integer, List<Feed>> result = null;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("status", 1);
    if (userId > 0) {
      condition.put("userId", userId);
      result = entityDao.findAndCount("feed", condition, page, PageNumberUtils.PAGE_SIZE_MEDIUM, FeedRowMapper.getInstance());

    } else {
      condition.put("source", "User");
      result = entityDao.findAndCount("feed", condition, page, PageNumberUtils.PAGE_SIZE_MEDIUM, FeedRowMapper.getInstance());
    }

    if (result != null) {
      request.setAttribute("feeds", result.right);
      int count = result.left;
      Pair<List<Integer>, Integer> pages = PageNumberUtils.generate(page, count, PageNumberUtils.PAGE_SIZE_MEDIUM);
      request.setAttribute("currentPage", page);
      request.setAttribute("pages", pages.left);
      request.setAttribute("lastPage", pages.right);
    }

    return Response.ok(new Viewable("user_feeds")).build();
  }

  @POST
  @Path("publish")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  @Transactional()
  public Map<String, Object> publish(@FormParam("feedId") long feedId, @FormParam("title") String title, @FormParam("description") String description, @FormParam("link") String link,
      @FormParam("image") String image, @FormParam("type") @DefaultValue("0") int type) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Feed feed = entityDao.get("feed", feedId, FeedRowMapper.getInstance());
    if (feed == null) {
      return ResponseBuilder.error(40404, "未找到动态。");
    }

    if (StringUtils.isEmpty(title)) {
      return ResponseBuilder.error(70101, "标题不能为空。");
    }
    if (StringUtils.length(title) < 5) {
      return ResponseBuilder.error(70102, "标题不能少于5个字。");
    }
    if (StringUtils.length(title) > 32) {
      return ResponseBuilder.error(70103, "标题不能超过32个字。");
    }
    if (StringUtils.isEmpty(description)) {
      return ResponseBuilder.error(70104, "标题不能为空。");
    }
    if (StringUtils.length(description) > 400) {
      return ResponseBuilder.error(70105, "描述不能超过400个字。");
    }
    if (StringUtils.isEmpty(link)) {
      return ResponseBuilder.error(70106, "链接不能为空。");
    }
    if (!StringUtils.startsWith(link, "http://") && !StringUtils.startsWith(link, "https://")) {
      return ResponseBuilder.error(70107, "链接不是一个合法的网页链接。");
    }
    if (StringUtils.length(link) > 400) {
      return ResponseBuilder.error(70108, "链接不能超过400个字符。");
    }

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("link", link);
    if (entityDao.exists("item", condition) || entityDao.exists("article", condition)) {
      return ResponseBuilder.error(70405, "已经发布过的资讯或文章。");
    }

    if (entityDao.exists("feed_to_item_review", "feedId", feedId)) {
      return ResponseBuilder.error(70406, "已经审核过的资讯。");
    }

    long time = feed.getCreateTime();
    Entity entity = new Entity();
    entity.set("userId", 100002).set("source", feed.getSource());
    entity.set("title", title).set("description", description).set("link", link).set("image", image);
    entity.set("rank", 0).set("publishTime", 0);
    entity.set("status", Constants.STATUS_ENABLED).set("createTime", time);

    if (type == 0) {
      entity.setModelName("item");
      entity = entityDao.saveAndReturn(entity);

      long itemId = entity.getId();
      if (StringUtils.isNotEmpty(image)) {
        Entity itemImage = new Entity("item_image");
        itemImage.set("itemId", itemId).set("path", image);
        itemImage.set("status", 1).set("createTime", time);
        entityDao.save(itemImage);
      }

      // 记录审核结果。
      time = System.currentTimeMillis();
      Entity review = new Entity("feed_to_item_review");
      review.set("feedId", feed.getId()).set("itemId", itemId).set("userId", user.getId()).set("value", 1);
      review.set("status", 1).set("createTime", time);
      entityDao.save(review);

      // 通知。
      if (feed.getUserId() >= 100000000) {
        Entity notification = new Entity("notification_system");
        notification.set("type", 1001).set("fromUserId", user.getId()).set("toUserId", feed.getUserId());
        String content = "你提交的投稿 " + title + " 已经通过审核，感谢你的投稿。";
        notification.set("dataId", feedId).set("content", content);
        notification.set("status", 0).set("createTime", time).set("lastVerifyTime", time);
        entityDao.save(notification);
      }

    } else if (type == 1) {
      entity.setModelName("article");
      entity = entityDao.saveAndReturn(entity);
    }

    // 删除草稿数据。
    entityDao.update("feed", "id", feedId, "status", Constants.STATUS_DELETED_BY_REVIEW);

    return ResponseBuilder.OK;
  }

  @POST
  @Path("remove")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  @Transactional()
  public Map<String, Object> remove(@FormParam("feedId") long feedId, @FormParam("title") String title, @FormParam("reason") int reason) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    Feed feed = entityDao.get("feed", feedId, FeedRowMapper.getInstance());
    if (feed == null) {
      return ResponseBuilder.error(40404, "未找到动态。");
    }

    if (entityDao.exists("feed_to_item_review", "feedId", feedId)) {
      return ResponseBuilder.error(70406, "已经审核过的资讯。");
    }

    // 记录审核结果。
    long time = System.currentTimeMillis();
    Entity review = new Entity("feed_to_item_review");
    review.set("feedId", feed.getId()).set("itemId", 0).set("userId", user.getId()).set("value", 0);
    review.set("status", 1).set("createTime", time);
    entityDao.save(review);

    // 删除资讯数据。
    entityDao.update("feed", "id", feedId, "status", Constants.STATUS_DELETED_BY_REVIEW);

    // 通知。
    if (feed.getUserId() >= 100000000) {
      Entity notification = new Entity("notification_system");
      notification.set("type", 1001).set("fromUserId", user.getId()).set("toUserId", feed.getUserId());
      String content = "你提交的投稿 " + feed.getTitle() + " 未通过审核，原因：";
      String reviewReason = Constants.REVIEW_REASONS.get(reason);
      content += StringUtils.isEmpty(reviewReason) ? Constants.REVIEW_REASONS.get(0) : reviewReason;
      notification.set("dataId", feedId).set("content", content);
      notification.set("status", 0).set("createTime", System.currentTimeMillis()).set("lastVerifyTime", System.currentTimeMillis());
      entityDao.save(notification);
    }

    return ResponseBuilder.OK;
  }

  @POST
  @Path("update")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  @Transactional()
  public Map<String, Object> update(@FormParam("feedId") long feedId, @FormParam("image") String image) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (StringUtils.isEmpty(image)) {
      return ResponseBuilder.error(40108, "图片不能为空。");
    }

    Feed feed = entityDao.get("feed", feedId, FeedRowMapper.getInstance());
    if (feed == null) {
      return ResponseBuilder.error(40404, "未找到动态。");
    }

    long time = System.currentTimeMillis();
    Map<String, Object> updateValues = new HashMap<String, Object>();
    updateValues.put("image", image);
    updateValues.put("updateTime", time);
    entityDao.update("feed", "id", feedId, updateValues);
    entityDao.update("feed_image", "feedId", feedId, "status", Constants.STATUS_DELETED_BY_ADMIN);

    Entity feedImage = new Entity("feed_image");
    feedImage.set("feedId", feedId).set("path", image);
    feedImage.set("status", 1).set("createTime", time);
    entityDao.save(feedImage);

    return ResponseBuilder.ok(ImageUtils.getImageUrl(image));
  }

}
