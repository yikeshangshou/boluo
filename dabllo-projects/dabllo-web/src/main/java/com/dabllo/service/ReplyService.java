package com.dabllo.service;

import java.util.List;

import com.dabllo.model.Reply;
import com.dabllo.util.Pair;

/**
 * @author mixueqiang
 * @since Aug 5, 2016
 */
public interface ReplyService {

  /**
   * 用户的观点。
   */
  Pair<Long, List<Reply>> getUserReplies(long userId, long offset);

}
