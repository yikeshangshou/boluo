package com.dabllo.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dabllo.dao.EntityDao;
import com.dabllo.dao.mapper.CommentRowMapper;
import com.dabllo.dao.mapper.FeedRowMapper;
import com.dabllo.model.Comment;
import com.dabllo.model.Entity;
import com.dabllo.model.Feed;
import com.dabllo.task.TaskScheduler;

/**
 * @author mixueqiang
 * @since Jun 6, 2016
 */
public class CommentNotificationBuildingTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(CommentNotificationBuildingTask.class);

  @Resource
  private EntityDao entityDao;

  public CommentNotificationBuildingTask() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 41, 23);
  }

  @Override
  public void run() {
    try {
      Map<String, Object> condition = new HashMap<String, Object>();
      condition.put("status", 1);
      List<Comment> comments = entityDao.find("comment", condition, 1, 10, CommentRowMapper.getInstance());
      if (CollectionUtils.isEmpty(comments)) {
        return;
      }

      LOG.info("Get " + comments.size() + " comments.");

      for (Comment comment : comments) {
        long commentId = comment.getId();
        try {
          if (entityDao.exists("notification_comment", "commentId", commentId)) {
            LOG.info("Skip comment " + commentId);
            continue;
          }

          LOG.info("Processing comment " + commentId);
          long feedId = comment.getFeedId();
          Feed feed = entityDao.get("feed", feedId, FeedRowMapper.getInstance());
          if (feed.getUserId() != comment.getUserId()) {
            Entity entity = new Entity("notification_comment");
            entity.set("commentId", comment).set("type", 1);
            entity.set("fromUserId", comment.getUserId()).set("toUserId", feed.getUserId());
            entity.set("feedId", feed.getId()).set("feed", StringUtils.substring(feed.getTitle(), 0, 20));
            entity.set("status", 1).set("createTime", System.currentTimeMillis());
            entityDao.save(entity);
          }

          condition = new HashMap<String, Object>();
          condition.put("feedId", feed.getId());
          condition.put("status", 1);
          List<Entity> feedUsers = null;
          int index = 1;
          do {
            feedUsers = entityDao.find("feed_user", condition, index++, 100);
            for (Entity feedUser : feedUsers) {
              long toUserId = feedUser.getLong("userId");
              if (toUserId != comment.getUserId()) {
                Entity entity = new Entity("notification_comment");
                entity.set("commentId", comment).set("type", 2);
                entity.set("fromUserId", comment.getUserId()).set("toUserId", toUserId);
                entity.set("feedId", feed.getId()).set("feed", StringUtils.substring(feed.getTitle(), 0, 20));
                entity.set("status", 1).set("createTime", System.currentTimeMillis());
                entityDao.save(entity);
              }
            }
          } while (CollectionUtils.isNotEmpty(feedUsers));

        } catch (Throwable t) {
          LOG.error("Error occurs on processing comment building task: " + commentId, t);
        }

      }

    } catch (Throwable t) {
      LOG.error("Error occurs on processing comment notification building task!", t);
    }
  }

}
