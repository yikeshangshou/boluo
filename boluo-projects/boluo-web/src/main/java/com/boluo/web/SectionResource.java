package com.boluo.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import com.sun.jersey.api.view.Viewable;

/**
 * @author mixueqiang
 * @since Jul 11, 2016
 */
@Path("/section")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class SectionResource extends BaseResource {

  @Resource
  protected SectionService sectionService;

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response index(@QueryParam("offset") long offset) {
    User user = getSessionUser();
    if (user == null) {
      return signinAndGoback();
    }

    long time = System.currentTimeMillis();
    Section topicSection = new Section("专题追踪", time);

    Map<String, Object> condtion = new HashMap<String, Object>();
    condtion.put("selected", Constants.STATUS_YES);
    condtion.put("status", Constants.STATUS_ENABLED);
    List<Topic> topics = entityDao.find("topic", condtion, 1, 6, TopicRowMapper.getInstance(), "rank", BaseDao.ORDER_OPTION_DESC);
    for (Topic topic : topics) {
      if (topic.getImage() == null) {
        topic.setImage("201608/1471319505074_RBISfBxe54.jpg");
      }
    }
    topicSection.setTopics(topics);
    request.setAttribute("topicSection", topicSection);

    List<Section> sections = new ArrayList<Section>();
    Section section3 = sectionService.getSection3(user.getId());
    Section section2 = sectionService.getSection2(user.getId());
    Section section1 = sectionService.getSection1(user.getId());
    sections.add(section1);
    sections.add(section2);
    sections.add(section3);
    request.setAttribute("sections", sections);

    Section discussionSection = new Section("最新讨论", time);
    condtion = new HashMap<String, Object>();
    condtion.put("status", Constants.STATUS_ENABLED);
    List<Discussion> discussions = entityDao.find("discussion", condtion, 1, 6, DiscussionRowMapper.getInstance(), "rank", BaseDao.ORDER_OPTION_DESC);
    for (Discussion discussion : discussions) {
      Item item = entityDao.get("item", discussion.getItemId(), ItemRowMapper.getInstance());
      if (item != null && item.getStatus() > Constants.STATUS_DISABLED) {
        discussion.getProperties().put("itemId", item.getId());
        discussion.getProperties().put("itemTitle", item.getTitle());
      }
    }
    discussionSection.setDiscussions(discussions);
    request.setAttribute("discussionSection", discussionSection);

    return Response.ok(new Viewable("index")).build();
  }

}
