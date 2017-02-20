package com.dabllo.task.section;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dabllo.dao.EntityDao;

/**
 * @author mixueqiang
 * @since Jul 20, 2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:ApplicationContext.xml" })
public class SectionLatestDeliveryTaskTest {

  @Resource
  EntityDao entityDao;

  @Test
  public void test() {
    new SectionLatestDeliveryTask(entityDao).run();
  }

}
