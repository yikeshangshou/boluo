package com.boluo.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.boluo.Constants;
import com.boluo.dao.BaseDao;
import com.boluo.dao.mapper.ItemRowMapper;
import com.boluo.model.Entity;
import com.boluo.model.Item;
import com.boluo.service.ItemService;
import com.boluo.service.SectionService;
import com.boluo.util.PageNumberUtils;
import com.boluo.util.Pair;

/**
 * @author mixueqiang
 * @since Jul 7, 2016
 */
@Service
public class ItemServiceImpl extends BaseService implements ItemService {
  private static final Log LOG = LogFactory.getLog(ItemServiceImpl.class);

  @Override
  public List<Item> getItems(String time, int page) {
    page = page > 0 ? page : 1;

    List<Entity> entities = null;
    if (StringUtils.equals(time, SectionService.TIME_3H)) {
      Map<String, Object> condition = new HashMap<String, Object>();
      Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
      long end = System.currentTimeMillis();
      long begin = end - 3 * HOUR;
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_DESC), end);
      entities = entityDao.findByOffset("section_16h", condition, offsets, page, PageNumberUtils.PAGE_SIZE_SMALL, "publishTime", BaseDao.ORDER_OPTION_DESC);

    } else if (StringUtils.equals(time, SectionService.TIME_8H + SectionService.TYPE_NEW)) {
      Map<String, Object> condition = new HashMap<String, Object>();
      Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
      long end = System.currentTimeMillis();
      long begin = end - 8 * HOUR;
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_DESC), end);
      entities = entityDao.findByOffset("section_16h", condition, offsets, page, PageNumberUtils.PAGE_SIZE_SMALL, "publishTime", BaseDao.ORDER_OPTION_DESC);

    } else if (StringUtils.equals(time, SectionService.TIME_8H + SectionService.TYPE_HOT)) {
      Map<String, Object> condition = new HashMap<String, Object>();
      Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
      long end = System.currentTimeMillis();
      long begin = end - 8 * HOUR;
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_DESC), end);
      entities = entityDao.findByOffset("section_16h", condition, offsets, page, PageNumberUtils.PAGE_SIZE_SMALL, "rank", BaseDao.ORDER_OPTION_DESC);

    } else if (StringUtils.equals(time, SectionService.TIME_16H)) {
      Map<String, Object> condition = new HashMap<String, Object>();
      Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
      long end = System.currentTimeMillis();
      long begin = end - 16 * HOUR;
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_DESC), end);
      entities = entityDao.findByOffset("section_16h", condition, offsets, page, PageNumberUtils.PAGE_SIZE_SMALL, "rank", BaseDao.ORDER_OPTION_DESC);

    } else if (StringUtils.equals(time, SectionService.TIME_24H)) {
      Map<String, Object> condition = new HashMap<String, Object>();
      Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
      long end = System.currentTimeMillis();
      long begin = end - 24 * HOUR;
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_DESC), end);
      entities = entityDao.findByOffset("section_24h", condition, offsets, page, PageNumberUtils.PAGE_SIZE_SMALL, "rank", BaseDao.ORDER_OPTION_DESC);

    } else if (StringUtils.equals(time, SectionService.TIME_1D)) {
      Map<String, Object> condition = new HashMap<String, Object>();
      long yesterday = System.currentTimeMillis() - 1 * DAY;
      String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(yesterday));
      condition.put("date", date);
      entities = entityDao.find("section_1d", condition, page, PageNumberUtils.PAGE_SIZE_MEDIUM, "rank", BaseDao.ORDER_OPTION_DESC);

    } else if (StringUtils.equals(time, SectionService.TIME_3D)) {
      Map<String, Object> condition = new HashMap<String, Object>();
      long yesterday = System.currentTimeMillis() - 1 * DAY;
      String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(yesterday));
      condition.put("date", date);
      entities = entityDao.find("section_3d", condition, page, PageNumberUtils.PAGE_SIZE_SMALL, "rank", BaseDao.ORDER_OPTION_DESC);

    } else if (StringUtils.equals(time, SectionService.TIME_7D)) {
      Map<String, Object> condition = new HashMap<String, Object>();
      long yesterday = System.currentTimeMillis() - 1 * DAY;
      String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(yesterday));
      condition.put("date", date);
      entities = entityDao.find("section_7d", condition, page, PageNumberUtils.PAGE_SIZE_SMALL, "rank", BaseDao.ORDER_OPTION_DESC);
    }

    List<Item> items = new ArrayList<Item>();
    if (CollectionUtils.isNotEmpty(entities)) {
      for (Entity entity : entities) {
        long itemId = entity.getLong("itemId");
        Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
        if (item != null && item.getStatus() == Constants.STATUS_ENABLED) {
          items.add(item);

          if (item.getHot() == 1) {
            item.getProperties().put("hot", 1);
          }
        }
      }
    }

    return items;
  }

  @Override
  public List<Item> getItemsBySection(String section, int page) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Pair<Long, List<Item>> getItemsByTopic(long topicId, long offset, int size) {
    // 已经没有下一页数据。
    if (offset < 0) {
      return new Pair<Long, List<Item>>(-1L, Collections.<Item> emptyList());
    }

    offset = offset > 0 ? offset : Long.MAX_VALUE;
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("topicId", topicId);
    condition.put("status", Constants.STATUS_ENABLED);
    List<Entity> entities = entityDao.findByOffset("topic_item", condition, "createTime", offset, size);

    if (CollectionUtils.isEmpty(entities)) {
      // 没有查询到数据，直接返回。
      return new Pair<Long, List<Item>>(-1L, Collections.<Item> emptyList());
    }

    // 查询数据。
    List<Item> items = new ArrayList<Item>();
    for (Entity entity : entities) {
      Item item = entityDao.get("item", entity.getLong("itemId"), ItemRowMapper.getInstance());
      if (item != null && item.isEnabled()) {
        items.add(item);

        condition = new HashMap<String, Object>();
        condition.put("itemId", item.getId());
        condition.put("status", Constants.STATUS_ENABLED);
        int count = entityDao.count("discussion", condition);
        item.getProperties().put("discussionCount", count);
      }

      // 更新offset：取最小的一个 createTime 为下一次查询的 offset。
      long createTime = entity.getLong("createTime");
      offset = offset > createTime ? createTime : offset;
    }

    return new Pair<Long, List<Item>>(offset, items);
  }

  @Override
  public void readItem(long userId, long itemId, int source, int value) {
    long time = System.currentTimeMillis();
    Entity entity = new Entity("item_read");
    entity.set("itemId", itemId).set("userId", userId);
    entity.set("source", source).set("value", value).set("time", time);
    entity.set("status", 0).set("createTime", time);
    entityDao.save(entity);
    LOG.info("User " + userId + " read item " + itemId + ": " + value);
  }

  @Override
  public void reportItem(long userId, long itemId, int type) {
    long time = System.currentTimeMillis();
    Entity entity = new Entity("item_report");
    entity.set("type", type).set("itemId", itemId).set("userId", userId);
    entity.set("status", 0).set("createTime", time);
    entityDao.save(entity);
    LOG.info("User " + userId + " report item " + itemId + ": " + type);
  }

}
