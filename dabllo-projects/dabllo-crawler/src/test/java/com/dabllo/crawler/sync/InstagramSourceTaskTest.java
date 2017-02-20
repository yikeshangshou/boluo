package com.dabllo.crawler.sync;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dabllo.crawler.instagram.InstagramSourceTask;
import com.dabllo.dao.EntityDao;

/**
 * @author mixueqiang
 * @since Jun 13, 2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:ApplicationContext.xml" })
public class InstagramSourceTaskTest {

  @Resource
  EntityDao entityDao;

  @Test
  public void test() {
    new InstagramSourceTask(entityDao).run();
  }

}
