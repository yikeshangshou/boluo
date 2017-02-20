package com.dabllo.web.api.v1;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.model.Entity;
import com.dabllo.model.User;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Jun 30, 2016
 */
@Path("/api/v1/ticket")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiTicketResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiTicketResource.class);

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@FormParam("contact") String contact, @FormParam("content") String content) {
    if (StringUtils.isEmpty(content)) {
      return ResponseBuilder.error(80102, "请输入您的意见或建议。");
    }
    if (StringUtils.length(content) > 100) {
      return ResponseBuilder.error(90103, "意见或建议内容不能超过100字。");
    }

    long time = System.currentTimeMillis();
    Entity ticket = new Entity("ticket");

    User user = getSessionUser();
    if (user != null) {
      ticket.set("userId", user.getId());
    }
    ticket.set("contact", contact).set("content", content);
    ticket.set("status", 1).set("createTime", time);
    entityDao.saveAndReturn(ticket);

    LOG.info("New ticket submitted: " + contact + "_" + content);
    return ResponseBuilder.OK;
  }

}
