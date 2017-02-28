package com.boluo.crawler.tech;

import org.junit.Test;

import com.boluo.crawler.tech.FeedCrawler36KrNews;

/**
 * @author mixueqiang
 * @since Sep 17, 2014
 */
public class FeedCrawler36KrNewsTest {

  @Test
  public void testGetFeed() {
    new FeedCrawler36KrNews().run();

  }

}
