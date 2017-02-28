package com.boluo.task.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.boluo.dao.EntityDao;
import com.boluo.dao.mapper.FeedRowMapper;
import com.boluo.dao.mapper.TopicRowMapper;
import com.boluo.model.Entity;
import com.boluo.model.Feed;
import com.boluo.model.Topic;
import com.boluo.task.TaskScheduler;

/**
 * @author mixueqiang
 * @since Jun 4, 2016
 */
public class TopicUpdateTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(TopicUpdateTask.class);

  @Resource
  private EntityDao entityDao;
  private int index = 1;

  public TopicUpdateTask() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 41, 4 * 60);
  }

  @Override
  public void run() {
    try {
      List<Topic> topics = entityDao.find("topic", "status", 1, TopicRowMapper.getInstance(), index++, 20);
      if (CollectionUtils.isEmpty(topics)) {
        index = 0;
        return;
      }

      for (Topic topic : topics) {
        List<Feed> feeds = entityDao.find("feed", "topicId", topic.getId(), FeedRowMapper.getInstance());
        if (CollectionUtils.isEmpty(feeds)) {
          continue;
        }
        entityDao.update("topic", "id", topic.getId(), "feedCount", feeds.size());
        LOG.info("Topic " + topic.getId() + " has " + feeds.size() + " feeds.");

        long time = System.currentTimeMillis();
        for (Feed feed : feeds) {
          // topic_feed
          Map<String, Object> condition = new HashMap<String, Object>();
          condition.put("topicId", topic.getId());
          condition.put("feedId", feed.getId());
          if (!entityDao.exists("topic_feed", condition)) {
            Entity entity = new Entity("topic_feed");
            entity.set("topicId", topic.getId()).set("feedId", feed.getId());
            entity.set("status", 1).set("createTime", time).set("updateTime", time);
            entityDao.save(entity);
          }

          // topic_user, user_topic
          Entity entity = new Entity();
          entity.set("topicId", topic.getId()).set("userId", feed.getUserId());
          entity.set("status", 1).set("createTime", time).set("updateTime", time);

          condition = new HashMap<String, Object>();
          condition.put("topicId", topic.getId());
          condition.put("userId", feed.getUserId());
          if (!entityDao.exists("topic_user", condition)) {
            entity.setModelName("topic_user");
            entity.set("type", 1);
            entityDao.save(entity);
          }

          if (!entityDao.exists("user_topic", condition)) {
            entity.setModelName("user_topic");
            entity.set("type", 2);
            entityDao.save(entity);
          }
        }
      }

    } catch (Throwable t) {
      LOG.error("Failed to update topic related databases!", t);
    }
  }

}
