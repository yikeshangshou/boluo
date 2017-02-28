package com.boluo.web.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.WebUtils;

import com.boluo.model.User;
import com.boluo.service.SessionService;

/**
 * @author mixueqiang
 * @since Oct 12, 2014
 */
public class SecurityFilter implements Filter {
  private static final Log LOG = LogFactory.getLog(SecurityFilter.class);

  private SessionService sessionService;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    ServletContext servletContext = filterConfig.getServletContext();
    WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);

    this.sessionService = (SessionService) applicationContext.getBean("sessionService");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;

    if (req.getSession(false) == null) {
      // Cookie自动登录。
      Cookie cookie = WebUtils.getCookie(req, "JSESSIONID");
      if (cookie != null) {
        // LOG.info("Cookie: " + cookie.getDomain() + "," + cookie.getPath() + "," + cookie.getValue());

        User user = sessionService.signinWithCookie(cookie.getValue());
        if (user != null) {
          WebUtils.setSessionAttribute(req, "_user", user);
        }
      }
    }

    // DeviceId 自动登录。
    User user = (User) WebUtils.getSessionAttribute(req, "_user");
    if (user == null) {
      String deviceId = req.getParameter("deviceId");
      if (deviceId != null) {
        user = sessionService.signinWithDeviceId(deviceId);
        if (user != null) {
          WebUtils.setSessionAttribute(req, "_user", user);
        }
      }
    }

    String requestURI = req.getRequestURI();
    if (requestURI.startsWith("/admin")) {
      user = (User) WebUtils.getSessionAttribute(req, "_user");
      if (user == null || !StringUtils.endsWith(user.getEmail(), "@dabllo.com")) {
        LOG.warn("No permission to access " + requestURI + ": " + user);
        resp.sendRedirect("/signin?to=" + requestURI);
        return;
      }
    }

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
  }

}
