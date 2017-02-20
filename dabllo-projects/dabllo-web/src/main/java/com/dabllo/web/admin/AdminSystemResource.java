package com.dabllo.web.admin;

import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.service.SectionService;
import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Jul 14, 2016
 */
@Path("/admin/system")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AdminSystemResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(AdminSystemResource.class);

  @Resource
  protected SectionService sectionService;

  @GET
  @Path("reset_cache")
  @Produces(APPLICATION_JSON)
  public Map<String, Object> resetCache(@QueryParam("time") String time) {
    if (sectionService.resetCache(time)) {
      LOG.info("Reset section cache completed: " + time);

      return ResponseBuilder.OK;
    }

    return ResponseBuilder.error(50000, "Failed to reset cache: " + time);
  }

}
