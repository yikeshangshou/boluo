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
 * @since Jun 3, 2016
 */
@Path("/api/v1/spam")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiSpamResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiSpamResource.class);

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@FormParam("business") int business, @FormParam("dataId") long dataId, @FormParam("type") int type, @FormParam("message") String message) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (business == 0) {
      return ResponseBuilder.error(90101, "缺少举报内容的业务类型。");
    }
    if (dataId == 0) {
      return ResponseBuilder.error(90102, "缺少举报内容的id。");
    }
    if (StringUtils.length(message) > 100) {
      return ResponseBuilder.error(90103, "举报说明信息不能超过100字。");
    }

    long time = System.currentTimeMillis();
    Entity spam = new Entity("spam");
    spam.set("userId", user.getId()).set("business", business).set("dataId", dataId).set("message", message);
    spam.set("status", 1).set("createTime", time);
    entityDao.saveAndReturn(spam);

    LOG.info("New spam submitted: " + business + "_" + dataId);
    return ResponseBuilder.OK;
  }

}
