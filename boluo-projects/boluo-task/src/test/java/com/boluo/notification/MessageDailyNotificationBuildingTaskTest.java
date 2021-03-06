package com.boluo.notification;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.boluo.dao.EntityDao;
import com.boluo.notification.MessageDailyNotificationBuildingTask;

/**
 * @author mixueqiang
 * @since Aug 6, 2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:ApplicationContext.xml" })
public class MessageDailyNotificationBuildingTaskTest {

  @Resource
  EntityDao entityDao;

  @Test
  public void test() {
    new MessageDailyNotificationBuildingTask(entityDao).run();
  }

}
