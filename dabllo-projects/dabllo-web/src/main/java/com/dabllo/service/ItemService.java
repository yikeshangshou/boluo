package com.dabllo.service;

import java.util.List;

import com.dabllo.model.Item;
import com.dabllo.util.Pair;

/**
 * @author mixueqiang
 * @since Jul 7, 2016
 */
public interface ItemService {

  List<Item> getItems(String time, int page);

  List<Item> getItemsBySection(String section, int page);

  Pair<Long, List<Item>> getItemsByTopic(long topicId, long offset, int size);

  void readItem(long userId, long itemId, int source, int value);

  void reportItem(long userId, long itemId, int type);

}
