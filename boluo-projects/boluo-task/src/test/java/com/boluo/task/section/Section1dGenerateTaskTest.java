package com.boluo.task.section;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.boluo.dao.EntityDao;
import com.boluo.task.section.Section1dGenerateTask;

/**
 * @author mixueqiang
 * @since Jul 20, 2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:ApplicationContext.xml" })
public class Section1dGenerateTaskTest {

  @Resource
  EntityDao entityDao;

  @Test
  public void test() {
    new Section1dGenerateTask(entityDao).run();
  }

}
