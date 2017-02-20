package com.dabllo.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dabllo.dao.EntityDao;
import com.dabllo.dao.mapper.FeedRowMapper;
import com.dabllo.model.Feed;
import com.dabllo.task.TaskScheduler;

/**
 * @author mixueqiang
 * @since Jun 6, 2016
 */
public class TopicNotificationBuildingTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(TopicNotificationBuildingTask.class);

  @Resource
  private EntityDao entityDao;

  public TopicNotificationBuildingTask() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 47, 23);
  }

  @Override
  public void run() {
    try {
      Map<String, Object> condition = new HashMap<String, Object>();
      condition.put("status", 1);
      List<Feed> feeds = entityDao.find("feed", condition, 1, 10, FeedRowMapper.getInstance());
      if (CollectionUtils.isEmpty(feeds)) {
        return;
      }

      LOG.info("Get " + feeds.size() + " feeds.");

      for (Feed feed : feeds) {
        long feedId = feed.getId();
        try {
          if (entityDao.exists("notification_feed", "feedId", feedId)) {
            LOG.info("Skip feed " + feedId);
            continue;
          }

          LOG.info("Processing feed " + feedId);

        } catch (Throwable t) {
          LOG.error("Error occurs on processing topic notification building task: " + feedId, t);
        }

      }

    } catch (Throwable t) {
      LOG.error("Error occurs on processing topic notification building tasks!", t);
    }
  }

}
