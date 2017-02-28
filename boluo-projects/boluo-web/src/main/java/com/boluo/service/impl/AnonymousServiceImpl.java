package com.boluo.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.boluo.Constants;
import com.boluo.model.Entity;
import com.boluo.service.AnonymousService;

/**
 * @author mixueqiang
 * @since Aug 12, 2016
 */
@Service
public class AnonymousServiceImpl extends BaseService implements AnonymousService {
  private static final Log LOG = LogFactory.getLog(AnonymousServiceImpl.class);

  @Override
  public int countReplyUp(String ip, long replyId) {
    if (StringUtils.isEmpty(ip) || replyId <= 0) {
      return 0;
    }

    String ipMatcher = getIpMatcher(ip);
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("replyId", replyId);
    condition.put("ipMatcher", ipMatcher);
    return entityDao.count("reply_up", condition);
  }

  @Override
  public void upReply(String ip, long replyId) {
    if (StringUtils.isEmpty(ip) || replyId <= 0) {
      return;
    }

    long time = System.currentTimeMillis();
    String ipMatcher = getIpMatcher(ip);

    Entity up = new Entity("reply_up");
    up.set("replyId", replyId).set("ip", ip).set("ipMatcher", ipMatcher).set("value", 1);
    up.set("status", Constants.STATUS_ENABLED).set("createTime", time);
    try {
      entityDao.save(up);

    } catch (Throwable t) {
      LOG.warn("Failed to process anonymous reply up.", t);
    }
  }

  private String getIpMatcher(String ip) {
    if (!StringUtils.contains(ip, ".")) {
      return ip;
    }

    return StringUtils.substringBeforeLast(ip, ".") + ".*";
  }

}
