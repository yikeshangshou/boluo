package com.dabllo.service;

import java.util.List;

import com.dabllo.model.Topic;
import com.dabllo.util.Pair;

/**
 * @author mixueqiang
 * @since Jun 15, 2016
 */
public interface TopicService {

  /**
   * 关注专题。
   * 
   * @param value
   *          1:关注,0:取消关注
   */
  int followTopic(long userId, long topicId, int value);

  /**
   * 用户关注的专题。
   */
  Pair<Long, List<Topic>> getFollowingTopics(long userId, long offset);

  /**
   * 阅读了专题。
   */
  void readTopic(long userId, long topicId);

}
