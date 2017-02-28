package com.boluo.crawler.tech;

import org.junit.Test;

import com.boluo.crawler.tech.FeedCrawlerBI;

/**
 * @author mixueqiang
 * @since Sep 17, 2014
 */
public class FeedCrawlerBITest {

  @Test
  public void testGetFeed() {
    new FeedCrawlerBI().run();

  }

}
