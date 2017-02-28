package com.boluo.service;

import java.util.List;

import com.boluo.model.Reply;
import com.boluo.util.Pair;

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
