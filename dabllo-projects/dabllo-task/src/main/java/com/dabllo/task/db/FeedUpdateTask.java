package com.dabllo.task.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
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
 * @since Jun 4, 2016
 */
public class FeedUpdateTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(FeedUpdateTask.class);

  @Resource
  private EntityDao entityDao;
  private int index = 1;

  public FeedUpdateTask() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 37, 5 * 60);
  }

  @Override
  public void run() {
    List<Feed> feeds = entityDao.find("feed", "status", 1, FeedRowMapper.getInstance(), index++, 20);
    if (CollectionUtils.isEmpty(feeds)) {
      index = 0;
      return;
    }

    for (Feed feed : feeds) {
      List<Comment> comments = entityDao.find("comment", "feedId", feed.getId(), CommentRowMapper.getInstance());
      if (CollectionUtils.isEmpty(comments)) {
        continue;
      }
      entityDao.update("feed", "id", feed.getId(), "commentCount", comments.size());
      LOG.info("Feed " + feed.getId() + " has " + comments.size() + " comments.");

      for (Comment comment : comments) {
        // feed_comment
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("feedId", feed.getId());
        condition.put("commentId", comment.getId());
        if (!entityDao.exists("feed_comment", condition)) {
          Entity entity = new Entity("feed_comment");
          entity.set("feedId", feed.getId()).set("commentId", comment.getId());
          entity.set("status", 1).set("createTime", System.currentTimeMillis());
          entityDao.save(entity);
        }
      }

      Map<String, Object> condition = new HashMap<String, Object>();
      condition.put("feedId", feed.getId());
      int count = entityDao.count("feed_like", condition);
      entityDao.update("feed", "id", feed.getId(), "likeCount", count);
      LOG.info("Feed " + feed.getId() + " has " + count + " likes.");
    }

  }

}
