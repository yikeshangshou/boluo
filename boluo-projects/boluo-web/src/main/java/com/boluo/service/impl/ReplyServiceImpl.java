package com.boluo.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.boluo.Constants;
import com.boluo.dao.mapper.ReplyRowMapper;
import com.boluo.model.Reply;
import com.boluo.service.ReplyService;
import com.boluo.util.PageNumberUtils;
import com.boluo.util.Pair;

/**
 * @author mixueqiang
 * @since Aug 3, 2016
 */
@Service
public class ReplyServiceImpl extends BaseService implements ReplyService {

  @Override
  public Pair<Long, List<Reply>> getUserReplies(long userId, long offset) {
    // 已经没有下一页数据。
    if (offset < 0) {
      return new Pair<Long, List<Reply>>(-1L, Collections.<Reply> emptyList());
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("userId", userId);
    condition.put("status", Constants.STATUS_ENABLED);
    List<Reply> replies = entityDao.findByOffset("reply", condition, offset, PageNumberUtils.PAGE_SIZE_MEDIUM, ReplyRowMapper.getInstance());

    if (CollectionUtils.isEmpty(replies)) {
      // 没有查询到数据，直接返回。
      return new Pair<Long, List<Reply>>(-1L, Collections.<Reply> emptyList());
    }

    for (Reply reply : replies) {
      // 更新offset：取最小的一个 id 为下一次查询的 offset。
      long id = reply.getId();
      offset = offset > id ? id : offset;
    }

    return new Pair<Long, List<Reply>>(offset, replies);
  }

}
