package com.boluo.service;

import java.util.List;

import com.boluo.model.Discussion;
import com.boluo.util.Pair;

/**
 * @author mixueqiang
 * @since Aug 3, 2016
 */
public interface DiscussionService {

  /**
   * 关注讨论。
   * 
   * @param value
   *          1:关注,0:取消关注
   */
  int followDiscussion(long userId, long discussionId, int value);

  /**
   * 专题的讨论列表。
   */
  Pair<Long, List<Discussion>> getDiscussionsByTopic(long topicId, long offset);

  /**
   * 用户发起的讨论列表。
   */
  Pair<Long, List<Discussion>> getDiscussionsByUser(long userId, long offset);

  /**
   * 用户关注的讨论列表。
   */
  Pair<Long, List<Discussion>> getFollowingDiscussions(long userId, long offset);

  /**
   * 全部已读。
   */
  void readAllDiscussions(long userId);

  /**
   * 阅读了讨论。
   */
  void readDiscussion(long userId, long discussionId);

}
