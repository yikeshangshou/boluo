package com.dabllo.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.dabllo.dao.mapper.FeedRowMapper;
import com.dabllo.dao.mapper.TopicRowMapper;
import com.dabllo.dao.mapper.UserRowMapper;
import com.dabllo.model.Entity;
import com.dabllo.model.Feed;
import com.dabllo.model.Topic;
import com.dabllo.model.User;
import com.dabllo.service.DiscoveryService;
import com.dabllo.util.PageNumberUtils;
import com.dabllo.util.Pair;

/**
 * @author mixueqiang
 * @since Jun 15, 2016
 */
@Service
public class DiscoveryServiceImpl extends BaseService implements DiscoveryService {
  private static final Log LOG = LogFactory.getLog(DiscoveryServiceImpl.class);

  @Override
  public Pair<Long, List<Feed>> getFeeds(long userId, long offset) {
    // 已经没有下一页数据。
    if (offset < 0) {
      return new Pair<Long, List<Feed>>(-1L, Collections.<Feed> emptyList());
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    List<Feed> feeds = null;
    if (userId > 0) {
      Map<String, Object> condition = new HashMap<String, Object>();
      condition.put("userId", userId);
      condition.put("status", 1);
      List<Entity> entities = entityDao.find("topic_follow", condition, 1, PageNumberUtils.PAGE_SIZE_SMALL);

      if (CollectionUtils.isNotEmpty(entities)) {
        feeds = entityDao.findNotIn("feed", userId, "topicId", entities, offset, PageNumberUtils.PAGE_SIZE_SMALL, FeedRowMapper.getInstance());
        LOG.info("User " + userId + " get " + feeds.size() + " feeds on discovery.");
      }
    }

    // 如果没有获取到Feed，则获取最新的Feed。
    if (CollectionUtils.isEmpty(feeds)) {
      Map<String, Object> condition = new HashMap<String, Object>();
      condition.put("status", 1);
      feeds = entityDao.findByOffset("feed", condition, offset, PageNumberUtils.PAGE_SIZE_SMALL, FeedRowMapper.getInstance());
    }

    if (CollectionUtils.isEmpty(feeds)) {
      // 没有查询到数据，直接返回。
      return new Pair<Long, List<Feed>>(-1L, Collections.<Feed> emptyList());
    }

    for (Feed feed : feeds) {
      // 查询Feed 图片信息。
      Map<String, Object> condition = new HashMap<String, Object>();
      condition.put("status", 1);
      condition.put("feedId", feed.getId());
      List<Entity> entities = entityDao.find("feed_image", condition, 1, 100);
      if (CollectionUtils.isNotEmpty(entities)) {
        for (Entity entity : entities) {
          feed.getImages().add(entity.getString("path"));
        }
      }

      // 查询Topic信息。
      Topic topic = entityDao.get("topic", feed.getTopicId(), TopicRowMapper.getInstance());
      if (topic != null) {
        feed.getProperties().put("topicId", topic.getId());
        feed.getProperties().put("topicTitle", topic.getTitle());
        feed.getProperties().put("topicUserCount", topic.getUserCount());
      }

      // 查询用户信息。
      User user = entityDao.get("user", feed.getUserId(), UserRowMapper.getInstance());
      if (user != null) {
        feed.setUsername(user.getUsername());
        feed.getProperties().put("username", user.getUsername());
        if (StringUtils.isNotEmpty(user.getAvatar())) {
          feed.getProperties().put("userAvatar", user.getAvatar());

        } else {
          feed.getProperties().put("userAvatar", "static/dabllo.png");
        }
      }

      // 查询这条动态是不是被他喜欢了
      condition = new HashMap<String, Object>();
      condition.put("userId", userId);
      condition.put("feedId", feed.getId());
      condition.put("status", 1);
      if (entityDao.exists("feed_like", condition)) {
        feed.setLikeStatus(1);
      } else {
        feed.setLikeStatus(0);
      }

      long id = feed.getId();
      offset = offset > id ? id : offset;
    }

    return new Pair<Long, List<Feed>>(offset, feeds);
  }

  @Override
  public Pair<Long, List<Feed>> getRecommendedFeeds(long userId, long offset, int size) {
    // TODO Auto-generated method stub
    return null;
  }

}
