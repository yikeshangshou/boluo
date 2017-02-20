package com.dabllo.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author mixueqiang
 * @since Mar 10, 2014
 */
public final class ResponseBuilder {
  public static final Map<String, Object> OK;
  public static final Map<String, Object> FAIL;
  public static final Map<String, Object> ERR_NEED_LOGIN;
  public static final Map<String, Object> ERR_PERMISSION_DENIED;
  public static final Map<String, Object> ERR_UNSUPPORTED_API;

  static {
    OK = new HashMap<String, Object>();
    OK.put("e", 0);

    FAIL = new HashMap<String, Object>();
    FAIL.put("e", -1);

    ERR_NEED_LOGIN = new HashMap<String, Object>();
    ERR_NEED_LOGIN.put("e", 10001);
    ERR_NEED_LOGIN.put("m", "您需要先登录，才能访问资源！");

    ERR_PERMISSION_DENIED = new HashMap<String, Object>();
    ERR_PERMISSION_DENIED.put("e", 10002);
    ERR_PERMISSION_DENIED.put("m", "您没有权限访问该资源！");

    ERR_UNSUPPORTED_API = new HashMap<String, Object>();
    ERR_UNSUPPORTED_API.put("e", 10003);
    ERR_UNSUPPORTED_API.put("m", "尚未支持的API！");
  }

  public static final Map<String, Object> error(int errorCode, String errorMessage) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("e", errorCode);
    result.put("m", errorMessage);
    return result;
  }

  public static final Map<String, Object> error(int errorCode, String errorMessage, Object data) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("e", errorCode);
    if (StringUtils.isNotEmpty(errorMessage)) {
      result.put("m", errorMessage);
    }
    if (data != null) {
      result.put("r", data);
    }
    return result;
  }

  public static final Map<String, Object> error(Throwable throwable) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("e", 500);
    result.put("m", throwable.getMessage());
    return result;
  }

  public static final Map<String, Object> ok(Object data) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("e", 0);
    if (data != null) {
      result.put("r", data);
    }
    return result;
  }

  public static final Map<String, Object> ok(Object data, long offset) {
    Map<String, Object> result = ok(data);
    result.put("o", offset);
    return result;
  }

  public static final Map<String, Object> ok(Object data, long offset, int count) {
    Map<String, Object> result = ok(data, offset);
    result.put("c", count);
    return result;
  }

  private ResponseBuilder() {
  }

}
