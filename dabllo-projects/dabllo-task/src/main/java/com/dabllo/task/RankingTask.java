package com.dabllo.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.dabllo.Constants;
import com.dabllo.dao.BaseDao;
import com.dabllo.dao.EntityDao;
import com.dabllo.dao.mapper.ItemRowMapper;
import com.dabllo.model.Entity;
import com.dabllo.model.Item;
import com.dabllo.util.PageNumberUtils;
import com.dabllo.util.Pair;

/**
 * @author mixueqiang
 * @since Jul 20, 2016
 */
@Service
public class RankingTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(RankingTask.class);

  @Resource
  private EntityDao entityDao;
  private Map<Long, Integer> ranks = new HashMap<Long, Integer>();

  public RankingTask() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 17, 2 * 60);
  }

  public RankingTask(EntityDao entityDao) {
    this.entityDao = entityDao;
  }

  @Override
  public void run() {
    ranks = new HashMap<Long, Integer>();
    LOG.info("Start to process ranking task.");

    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("status", Constants.STATUS_ENABLED);
    Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
    long end = System.currentTimeMillis();
    long begin = end - 2 * 60 * 1000L;
    offsets.put(new Pair<String, String>("createTime", BaseDao.ORDER_OPTION_ASC), begin);
    offsets.put(new Pair<String, String>("createTime", BaseDao.ORDER_OPTION_DESC), end);

    List<Entity> entities = entityDao.findByOffset("discussion", condition, offsets, PageNumberUtils.PAGE_SIZE_XXLARGE);
    if (CollectionUtils.isNotEmpty(entities)) {
      for (Entity entity : entities) {
        long itemId = entity.getLong("itemId");
        if (itemId > 0) {
          incRank(itemId, 5);
        }
      }
    }

    entities = entityDao.findByOffset("reply", condition, offsets, PageNumberUtils.PAGE_SIZE_XXLARGE);
    if (CollectionUtils.isNotEmpty(entities)) {
      for (Entity entity : entities) {
        long itemId = entity.getLong("itemId");
        if (itemId > 0) {
          incRank(itemId, 2);
        }
      }
    }

    entities = entityDao.find("item_read", "status", Constants.STATUS_DISABLED, 1, PageNumberUtils.PAGE_SIZE_MEDIUM);
    if (CollectionUtils.isNotEmpty(entities)) {
      for (Entity entity : entities) {
        long itemId = entity.getLong("itemId");
        if (itemId > 0) {
          incRank(itemId, 1);
        }
        entityDao.update("item_read", "id", entity.getId(), "status", Constants.STATUS_ENABLED);
      }
    }

    entities = entityDao.find("item_up", "status", Constants.STATUS_DISABLED, 1, PageNumberUtils.PAGE_SIZE_MEDIUM);
    if (CollectionUtils.isNotEmpty(entities)) {
      for (Entity entity : entities) {
        long itemId = entity.getLong("itemId");
        if (itemId > 0) {
          int value = entity.getInt("value");
          int count = value == 1 ? 2 : -2;
          incRank(itemId, count);
        }

        entityDao.update("item_up", "id", entity.getId(), "status", Constants.STATUS_ENABLED);
      }
    }

    for (long itemId : ranks.keySet()) {
      int rank = ranks.get(itemId);
      entityDao.update("item", "id", itemId, "rank", rank);
      entityDao.update("section_16h", "itemId", itemId, "rank", rank);
      entityDao.update("section_24h", "itemId", itemId, "rank", rank);
      LOG.info("Ranking change: " + itemId + ", " + rank);
    }
  }

  private void incRank(long itemId, int count) {
    int rank = 0;
    if (ranks.containsKey(itemId)) {
      rank = ranks.get(itemId);

    } else {
      Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
      rank = item.getRank();
    }

    rank += count;
    rank = rank > 0 ? rank : 0;
    ranks.put(itemId, rank);
  }
}
