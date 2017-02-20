package com.dabllo.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author mixueqiang
 * @since Jun 24, 2016
 */
public class ApiFeedTest {
  private static final String URL_FEED_POST = "http://www.aqwa.cn/api/v1/feed";

  @Test
  public void testPostFeed() throws Throwable {
    Map<String, String> feed = new HashMap<String, String>();
    feed.put("category", "all");
    feed.put("topicId", "0");
    feed.put("title", "这是一条测试Feed");
    feed.put("link", "");
    feed.put("hidden", "0");
    feed.put("status", "1");

    HttpClientUtils.signin();
    HttpClientUtils.post(URL_FEED_POST, feed);
  }

}
