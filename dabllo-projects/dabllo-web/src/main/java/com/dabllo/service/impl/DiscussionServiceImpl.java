package com.dabllo.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.dabllo.Constants;
import com.dabllo.dao.mapper.DiscussionRowMapper;
import com.dabllo.model.Discussion;
import com.dabllo.model.Entity;
import com.dabllo.service.DiscussionService;
import com.dabllo.util.PageNumberUtils;
import com.dabllo.util.Pair;

/**
 * @author mixueqiang
 * @since Aug 3, 2016
 */
@Service
public class DiscussionServiceImpl extends BaseService implements DiscussionService {

  @Override
  public int followDiscussion(long userId, long discussionId, int value) {
    int status = value == 1 ? 1 : 0;
    long time = System.currentTimeMillis();

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("discussionId", discussionId);
    condition.put("userId", userId);
    if (entityDao.exists("discussion_follow", condition)) {
      entityDao.update("discussion_follow", condition, "status", status);

    } else {
      Entity entity = new Entity("discussion_follow");
      entity.set("userId", userId).set("discussionId", discussionId);
      entity.set("status", status).set("createTime", time).set("updateTime", time);
      entityDao.save(entity);
    }

    return status;
  }

  @Override
  public Pair<Long, List<Discussion>> getFollowingDiscussions(long userId, long offset) {
    // 已经没有下一页数据。
    if (offset < 0) {
      return new Pair<Long, List<Discussion>>(-1L, Collections.<Discussion> emptyList());
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("userId", userId);
    condition.put("status", Constants.STATUS_ENABLED);
    List<Entity> entities = entityDao.findByOffset("discussion_follow", condition, "updateTime", offset, PageNumberUtils.PAGE_SIZE_MEDIUM);

    if (CollectionUtils.isEmpty(entities)) {
      // 没有查询到数据，直接返回。
      return new Pair<Long, List<Discussion>>(-1L, Collections.<Discussion> emptyList());
    }

    // 查询Discussion数据。
    List<Discussion> discussions = new ArrayList<Discussion>();
    for (Entity entity : entities) {
      Discussion discussion = entityDao.get("discussion", entity.getLong("discussionId"), DiscussionRowMapper.getInstance());
      if (discussion != null && discussion.getStatus() >= Constants.STATUS_ENABLED) {
        discussions.add(discussion);
      }

      // 更新offset：取最小的一个 updateTime 为下一次查询的 offset。
      long updateTime = entity.getLong("updateTime");
      offset = offset > updateTime ? updateTime : offset;
    }

    return new Pair<Long, List<Discussion>>(offset, discussions);
  }

  @Override
  public Pair<Long, List<Discussion>> getDiscussionsByTopic(long topicId, long offset) {
    // 已经没有下一页数据。
    if (offset < 0) {
      return new Pair<Long, List<Discussion>>(-1L, Collections.<Discussion> emptyList());
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("topicId", topicId);
    condition.put("status", Constants.STATUS_ENABLED);
    List<Entity> entities = entityDao.find("topic_item", condition, 1, PageNumberUtils.PAGE_SIZE_XXLARGE);
    List<Discussion> discussions = entityDao.findOrIn("discussion", "topicId", topicId, "itemId", entities, offset, PageNumberUtils.PAGE_SIZE_MEDIUM, DiscussionRowMapper.getInstance());

    if (CollectionUtils.isEmpty(discussions)) {
      // 没有查询到数据，直接返回。
      return new Pair<Long, List<Discussion>>(-1L, Collections.<Discussion> emptyList());
    }

    for (Discussion discussion : discussions) {
      // 更新offset：取最小的一个 id 为下一次查询的 offset。
      long id = discussion.getId();
      offset = offset > id ? id : offset;
    }

    return new Pair<Long, List<Discussion>>(offset, discussions);
  }

  @Override
  public Pair<Long, List<Discussion>> getDiscussionsByUser(long userId, long offset) {
    // 已经没有下一页数据。
    if (offset < 0) {
      return new Pair<Long, List<Discussion>>(-1L, Collections.<Discussion> emptyList());
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("userId", userId);
    condition.put("status", Constants.STATUS_ENABLED);
    List<Discussion> discussions = entityDao.findByOffset("discussion", condition, offset, PageNumberUtils.PAGE_SIZE_MEDIUM, DiscussionRowMapper.getInstance());

    if (CollectionUtils.isEmpty(discussions)) {
      // 没有查询到数据，直接返回。
      return new Pair<Long, List<Discussion>>(-1L, Collections.<Discussion> emptyList());
    }

    for (Discussion discussion : discussions) {
      // 更新offset：取最小的一个 id 为下一次查询的 offset。
      long id = discussion.getId();
      offset = offset > id ? id : offset;
    }

    return new Pair<Long, List<Discussion>>(offset, discussions);
  }

  @Override
  public void readAllDiscussions(long userId) {
    long time = System.currentTimeMillis();

    if (entityDao.exists("user_operation", "userId", userId)) {
      entityDao.update("user_operation", "userId", userId, "readAllTime", time);

    } else {
      Entity entity = new Entity("user_operation");
      entity.set("userId", userId).set("readAllTime", time);
      entity.set("createTime", time);
      entityDao.save(entity);
    }
  }

  @Override
  public void readDiscussion(long userId, long discussionId) {
    long time = System.currentTimeMillis();

    // 更新讨论阅读。
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("discussionId", discussionId);
    condition.put("userId", userId);
    if (entityDao.exists("discussion_read", condition)) {
      Map<String, Object> updateValues = new HashMap<String, Object>();
      updateValues.put("updateTime", time);
      entityDao.update("discussion_read", condition, updateValues);

    } else {
      Entity entity = new Entity("discussion_read");
      entity.set("userId", userId).set("discussionId", discussionId);
      entity.set("status", Constants.STATUS_ENABLED).set("createTime", time).set("updateTime", time);
      entityDao.save(entity);
    }

    // 更新讨论关注。
    entityDao.update("discussion_follow", condition, "lastReadTime", time);
  }

}
