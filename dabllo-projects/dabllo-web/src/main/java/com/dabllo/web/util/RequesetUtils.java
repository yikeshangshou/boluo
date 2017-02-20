package com.dabllo.web.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * @author mixueqiang
 * @since Aug 12, 2016
 */
public final class RequesetUtils {
  private static final String[] HEADER_NAMES = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR", "x-real-ip" };

  public static String getClientIp(HttpServletRequest request) {
    if (request == null) {
      return null;
    }

    String ip = null;
    for (String headerName : HEADER_NAMES) {
      ip = request.getHeader(headerName);

      if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(ip, "unknown")) {
        continue;

      } else {
        break;
      }
    }

    if (StringUtils.isNotBlank(ip)) {
      ip = StringUtils.split(ip, ',')[0];
      return ip;
    }

    return request.getRemoteAddr();
  }

}
