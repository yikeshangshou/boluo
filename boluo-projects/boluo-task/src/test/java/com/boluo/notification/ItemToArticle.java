package com.boluo.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.boluo.dao.EntityDao;
import com.boluo.dao.mapper.EntityRowMapper;
import com.boluo.dao.mapper.ItemRowMapper;
import com.boluo.model.Entity;
import com.boluo.model.Item;

/**
 * @author mixueqiang
 * @since Aug 6, 2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:ApplicationContext.xml" })
public class ItemToArticle {

  @Resource
  EntityDao entityDao;

  @Test
  public void test() { 
	  Map<String, Object> condition = new HashMap<String, Object>();
	  condition.put("id", 843);
	  Item item = entityDao.findOne("item", condition, ItemRowMapper.getInstance());
	  Entity entity = new Entity("article");
	  entity.set("userId", 100002).set("source", item.getSource()).set("title", item.getTitle()).set("rank", 0).set("publishTime", 0);
	  entity.set("description", item.getDescription()).set("link", item.getLink()).set("createTime", item.getCreateTime());
	  entity.set("status", 1).set("image", item.getImage()); 
	  Entity article = entityDao.saveAndReturn(entity);
	  
	  Map<String, Object> condition2 = new HashMap<String, Object>();
	  condition2.put("itemId", item.getId());
	  List<Entity> topics = entityDao.find("topic_item", condition2, EntityRowMapper.getInstance());
	  for (Entity topic : topics) {
		  Entity entityTA = new Entity("topic_article");
		  entityTA.set("topicId", topic.getLong("topicId")).set("articleId", article.getId()).set("status", 1).set("createTime", item.getCreateTime());
		  
		  Map<String, Object> condition3 = new HashMap<String, Object>();
		  condition3.put("itemId", item.getId());
		  condition3.put("id", topic.getLong("id"));
		  
		  entityDao.save(entityTA);
		  entityDao.delete("topic_item", condition3);
	}
	  entityDao.delete("item", condition);
	  
	  
  }

}
