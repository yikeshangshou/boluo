package com.boluo.service;

import java.util.List;

import com.boluo.model.Feed;
import com.boluo.util.Pair;

/**
 * @author mixueqiang
 * @since Jun 15, 2016
 */
public interface DiscoveryService {

  Pair<Long, List<Feed>> getFeeds(long userId, long offset);
  
  Pair<Long, List<Feed>> getRecommendedFeeds(long userId, long offset, int size);

}
