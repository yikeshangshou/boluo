package com.boluo.web.api.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.boluo.Constants;
import com.boluo.dao.BaseDao;
import com.boluo.dao.mapper.DiscussionRowMapper;
import com.boluo.dao.mapper.ItemRowMapper;
import com.boluo.dao.mapper.TopicRowMapper;
import com.boluo.model.Discussion;
import com.boluo.model.Item;
import com.boluo.model.Section;
import com.boluo.model.Topic;
import com.boluo.model.User;
import com.boluo.service.SectionService;
import com.boluo.util.ResponseBuilder;
import com.boluo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Jul 6, 2016
 */
@Path("/api/v1/section")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiSectionResource extends BaseResource {

  @Resource
  protected SectionService sectionService;

  @GET
  @Path("discussion")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getDiscussions() {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    long time = System.currentTimeMillis();
    List<Section> sections = new ArrayList<Section>();
    Section section = new Section("最新讨论", time);

    Map<String, Object> condtion = new HashMap<String, Object>();
    condtion.put("status", Constants.STATUS_ENABLED);
    List<Discussion> discussions = entityDao.find("discussion", condtion, 1, 6, DiscussionRowMapper.getInstance(), "id", BaseDao.ORDER_OPTION_DESC);
    for (Discussion discussion : discussions) {
      setTopicOrItemProperties(discussion);
    }
    section.setDiscussions(discussions);
    section.getProperties().put("position", "bottom");
    sections.add(section);

    return ResponseBuilder.ok(sections);
  }

  @GET
  @Path("topic")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getTopics() {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    long time = System.currentTimeMillis();
    List<Section> sections = new ArrayList<Section>();
    Section section = new Section("专题追踪", time);

    Map<String, Object> condtion = new HashMap<String, Object>();
    condtion.put("selected", Constants.STATUS_YES);
    condtion.put("status", Constants.STATUS_ENABLED);
    List<Topic> topics = entityDao.find("topic", condtion, 1, 6, TopicRowMapper.getInstance(), "rank", BaseDao.ORDER_OPTION_DESC);
    section.setTopics(topics);
    section.getProperties().put("position", "top");
    sections.add(section);

    return ResponseBuilder.ok(sections);
  }

  @GET
  @Path("list")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> list() {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    long userId = user.getId();
    List<Section> sections = new ArrayList<Section>();
    Section section3 = sectionService.getSection3(userId);
    Section section2 = sectionService.getSection2(userId);
    Section section1 = sectionService.getSection1(userId);
    sections.add(section1);
    sections.add(section2);
    sections.add(section3);

    return ResponseBuilder.ok(sections);
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

}
