package com.dabllo.web.api.v1;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.Constants;
import com.dabllo.model.Entity;
import com.dabllo.model.User;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Aug 18, 2016
 */
@Path("/api/v1/supertag")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiSupertagResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiSupertagResource.class);

  @GET
  @Path("set")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> set(@PathParam("type") int type, @PathParam("dataId") long dataId, @PathParam("dataType") String dataType, @QueryParam("value") @DefaultValue("1") int value) {
    User user = getSessionUser();
    if (user == null) {
      return ResponseBuilder.ERR_NEED_LOGIN;
    }

    if (!StringUtils.equals("hot", dataType) && !StringUtils.equals("latest", dataType)) {
      return ResponseBuilder.error(50000, "不支持的dataType: " + dataType);
    }

    value = value == 1 ? 1 : 0;
    try {
      Map<String, Object> condition = new HashMap<String, Object>();
      condition.put("type", type);
      condition.put("dataId", dataId);
      if (entityDao.exists("supertag", condition)) {
        entityDao.update("supertag", condition, dataType, value);

        LOG.info("Admin " + user.getId() + " update supertag, " + type + "_" + dataId + "_" + dataType + ":" + value);
      } else {
        Entity entity = new Entity("supertag");
        entity.set("type", type).set("dataId", dataId).set(dataType, value);
        entity.set("status", Constants.STATUS_ENABLED).set("createTime", System.currentTimeMillis());
        entityDao.save(entity);

        LOG.info("Admin " + user.getId() + " create supertag, " + type + "_" + dataId + "_" + dataType + ":" + value);
      }

      return ResponseBuilder.ok(value);

    } catch (Throwable t) {
      LOG.error("Failed to save supertag, " + type + "_" + dataId + "_" + dataType + ":" + value, t);
      return ResponseBuilder.error(50000, "Failed to save supertag.");
    }
  }

}
