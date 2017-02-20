package com.dabllo.web.api.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.Constants;
import com.dabllo.dao.mapper.TopicRowMapper;
import com.dabllo.model.Topic;
import com.dabllo.util.PageNumberUtils;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Aug 17, 2016
 */
@Path("/api/v1/tag")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiTagResource extends BaseResource {

  @GET
  @Path("hot")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getTags() {
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("status", Constants.STATUS_ENABLED);
    List<Topic> topics = entityDao.find("topic", condition, 1, PageNumberUtils.PAGE_SIZE_SMALL, TopicRowMapper.getInstance());

    return ResponseBuilder.ok(topics);
  }

  @GET
  @Produces(APPLICATION_JSON)
  public Map<String, Object> getTags(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("size") @DefaultValue("20") int size) {
    page = page > 0 ? page : 1;
    size = (size > 0 && size <= 100) ? size : PageNumberUtils.PAGE_SIZE_MEDIUM;

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("status", Constants.STATUS_ENABLED);
    List<Topic> topics = entityDao.find("topic", condition, page, size, TopicRowMapper.getInstance());

    return ResponseBuilder.ok(topics);
  }

}
