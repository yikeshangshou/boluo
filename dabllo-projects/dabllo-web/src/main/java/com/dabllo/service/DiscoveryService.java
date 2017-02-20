package com.dabllo.service;

import java.util.List;

import com.dabllo.model.Feed;
import com.dabllo.util.Pair;

/**
 * @author mixueqiang
 * @since Jun 15, 2016
 */
public interface DiscoveryService {

  Pair<Long, List<Feed>> getFeeds(long userId, long offset);
  
  Pair<Long, List<Feed>> getRecommendedFeeds(long userId, long offset, int size);

}
