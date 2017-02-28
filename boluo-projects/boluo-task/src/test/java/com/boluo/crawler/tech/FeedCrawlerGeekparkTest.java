package com.boluo.crawler.tech;

import org.junit.Test;

import com.boluo.crawler.tech.FeedCrawlerGeekpark;

/**
 * @author mixueqiang
 * @since Sep 17, 2014
 */
public class FeedCrawlerGeekparkTest {

  @Test
  public void testGetFeed() {
    new FeedCrawlerGeekpark().run();

  }

}
