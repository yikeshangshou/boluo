package com.dabllo.web.api.v1;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dabllo.util.ResponseBuilder;
import com.dabllo.web.BaseResource;

/**
 * @author mixueqiang
 * @since Jul 14, 2016
 */
@Path("/api/v1/page")
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApiPageResource extends BaseResource {
  private static final Log LOG = LogFactory.getLog(ApiPageResource.class);

  @GET
  @Path("preview")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(APPLICATION_JSON)
  public Map<String, Object> save(@QueryParam("url") String url) {
    if (StringUtils.isEmpty(url)) {
      return ResponseBuilder.error(50000, "参数 url 不可以为空。");
    }

    if (!StringUtils.startsWith(url, "http://") && !StringUtils.startsWith(url, "https://")) {
      url = "http://" + url;
    }

    Map<String, String> result = new HashMap<String, String>();
    result.put("url", url);

    Document document = null;
    try {
      document = Jsoup.parse(new URL(url), 1000);
      Elements elements = document.select("title");
      if (elements != null && elements.size() > 0) {
        String title = elements.first().text();
        result.put("title", title);
      }

      elements = document.select("meta[property=og:title]");
      if (elements != null && elements.size() > 0) {
        String title = elements.first().attr("content");
        result.put("title", title);
      }

      elements = document.select("meta[name=description]");
      if (elements != null && elements.size() > 0) {
        String description = elements.first().attr("content");
        result.put("description", description);
      }

      return ResponseBuilder.ok(result);

    } catch (Throwable t) {
      LOG.warn("Failed to get page: " + url, t);
    }

    return ResponseBuilder.error(500, "Failed to get page data!");
  }

}
