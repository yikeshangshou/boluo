package com.dabllo.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.dabllo.Constants;
import com.dabllo.dao.mapper.TopicRowMapper;
import com.dabllo.model.Entity;
import com.dabllo.model.Topic;
import com.dabllo.service.TopicService;
import com.dabllo.util.PageNumberUtils;
import com.dabllo.util.Pair;

/**
 * @author mixueqiang
 * @since Jun 15, 2016
 */
@Service
public class TopicServiceImpl extends BaseService implements TopicService {

  @Override
  public int followTopic(long userId, long topicId, int value) {
    int status = value == 1 ? 1 : 0;
    long time = System.currentTimeMillis();

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("topicId", topicId);
    condition.put("userId", userId);
    if (entityDao.exists("topic_follow", condition)) {
      entityDao.update("topic_follow", condition, "status", status);

    } else {
      Entity entity = new Entity("topic_follow");
      entity.set("userId", userId).set("topicId", topicId);
      entity.set("status", status).set("createTime", time).set("updateTime", time);
      entityDao.save(entity);
    }

    return status;
  }

  @Override
  public Pair<Long, List<Topic>> getFollowingTopics(long userId, long offset) {
    // 已经没有下一页数据。
    if (offset < 0) {
      return new Pair<Long, List<Topic>>(-1L, Collections.<Topic> emptyList());
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("userId", userId);
    condition.put("status", Constants.STATUS_ENABLED);
    List<Entity> entities = entityDao.findByOffset("topic_follow", condition, "updateTime", offset, PageNumberUtils.PAGE_SIZE_SMALL);

    if (CollectionUtils.isEmpty(entities)) {
      // 没有查询到数据，直接返回。
      return new Pair<Long, List<Topic>>(-1L, Collections.<Topic> emptyList());
    }

    // 查询Topic数据。
    List<Topic> topics = new ArrayList<Topic>();
    for (Entity entity : entities) {
      Topic topic = entityDao.get("topic", entity.getLong("topicId"), TopicRowMapper.getInstance());
      if (topic != null) {
        if (topic.getImage() == null) {
          topic.setImage("201608/1471319505074_RBISfBxe54.jpg");
        }

        topics.add(topic);
      }

      // 更新offset：取最小的一个 updateTime 为下一次查询的 offset。
      long updateTime = entity.getLong("updateTime");
      offset = offset > updateTime ? updateTime : offset;
    }

    return new Pair<Long, List<Topic>>(offset, topics);
  }

  @Override
  public void readTopic(long userId, long topicId) {
    long time = System.currentTimeMillis();

    // 更新专题阅读。
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("topicId", topicId);
    condition.put("userId", userId);
    if (entityDao.exists("topic_read", condition)) {
      Map<String, Object> updateValues = new HashMap<String, Object>();
      updateValues.put("updateTime", time);
      entityDao.update("topic_read", condition, updateValues);

    } else {
      Entity entity = new Entity("topic_read");
      entity.set("userId", userId).set("topicId", topicId);
      entity.set("status", Constants.STATUS_ENABLED).set("createTime", time).set("updateTime", time);
      entityDao.save(entity);
    }

    // 更新专题关注。
    entityDao.update("topic_follow", condition, "lastReadTime", time);
  }

}
