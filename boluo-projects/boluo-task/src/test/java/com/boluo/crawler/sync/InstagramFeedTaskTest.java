package com.boluo.crawler.sync;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.boluo.crawler.sync.InstagramFeedTask;
import com.boluo.dao.EntityDao;

/**
 * @author mixueqiang
 * @since Jun 13, 2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:ApplicationContext.xml" })
public class InstagramFeedTaskTest {

  @Resource
  EntityDao entityDao;

  @Test
  public void test() {
    new InstagramFeedTask(entityDao, 1).run();
  }

}
