package com.dabllo.task.db;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dabllo.dao.EntityDao;
import com.dabllo.dao.mapper.TopicRowMapper;
import com.dabllo.model.Topic;
import com.dabllo.task.TaskScheduler;

/**
 * @author mixueqiang
 * @since Jun 4, 2016
 */
public class TopicUserCountUpdateTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(TopicUserCountUpdateTask.class);

  @Resource
  private EntityDao entityDao;
  private int index = 1;

  public TopicUserCountUpdateTask() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 33, 3 * 60);
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
        int count = entityDao.count("topic_user", "topicId", topic.getId());
        entityDao.update("topic", "id", topic.getId(), "userCount", count);
        LOG.info("Topic " + topic.getId() + " has " + count + " users.");
      }

    } catch (Throwable t) {
      LOG.error("Failed to update topic related databases!", t);
    }

  }

}
