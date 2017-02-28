package com.boluo.web;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.WebUtils;

import com.boluo.dao.EntityDao;
import com.boluo.model.User;

/**
 * 
 * @author mixueqiang
 * @since Oct 12, 2014
 */
public abstract class BaseResource {
  public final static String APPLICATION_JSON = "application/json; charset=utf-8";

  protected static Map<String, Long> categories = new TreeMap<String, Long>(new Comparator<String>() {
    @Override
    public int compare(String o1, String o2) {
      return o1.compareTo(o2);
    }
  });

  static {
    categories.put("w", 1L);
    categories.put("m", 2L);
    categories.put("e", 3L);
    categories.put("f", 4L);
    categories.put("b", 5L);
  }

  @Resource
  protected EntityDao entityDao;
  @Context
  protected HttpServletRequest request;

  protected Map<String, Object> filterUser(User user) {
    if (user == null) {
      return null;
    }

    Map<String, Object> result = new HashMap<String, Object>();
    result.put("id", user.getId());
    result.put("username", user.getUsername());
    result.put("avatar", user.getAvatar());
    return result;
  }

  public long getCategoryId(String category) {
    if (categories.containsKey(category)) {
      return categories.get(category);
    }

    return 1L;
  }

  public String getClientIp() {
    String ip = request.getHeader("x-forwarded-for");
    if (StringUtils.isEmpty(ip) || StringUtils.equals(ip, "unknown")) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (StringUtils.isEmpty(ip) || StringUtils.equals(ip, "unknown")) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (StringUtils.isEmpty(ip) || StringUtils.equals(ip, "unknown")) {
      ip = request.getRemoteAddr();
    }

    return ip;
  }

  public Object getSessionAttribute(String name) {
    return WebUtils.getSessionAttribute(request, name);
  }

  public User getSessionUser() {
    return (User) getSessionAttribute("_user");
  }

  public long getSessionUserId() {
    User user = getSessionUser();
    if (user != null) {
      return user.getId();
    }

    return -1L;
  }

  public Response redirect(String to) {
    try {
      return Response.seeOther(new URI(to)).build();
    } catch (Throwable t) {
      return null;
    }
  }

  public void setSessionAttribute(String name, Object value) {
    WebUtils.setSessionAttribute(request, name, value);
  }

  public Response signinAndGoback() {
    return signinAndGoto(request.getRequestURI(), request.getQueryString());
  }

  public Response signinAndGoto(String to) {
    try {
      return Response.seeOther(new URI("/signin?to=" + URLEncoder.encode(to, "utf-8"))).build();
    } catch (Throwable t) {
      return null;
    }
  }

  public Response signinAndGoto(String requestUri, String queryString) {
    String to = requestUri;
    if (StringUtils.isNotEmpty(queryString)) {
      to += "?" + queryString;
    }

    return signinAndGoto(to);
  }

}
