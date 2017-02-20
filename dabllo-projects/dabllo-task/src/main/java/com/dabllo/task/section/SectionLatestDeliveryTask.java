package com.dabllo.task.section;

import java.util.ArrayList;
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
import com.dabllo.model.Entity;
import com.dabllo.task.TaskScheduler;
import com.dabllo.util.PageNumberUtils;
import com.dabllo.util.Pair;

/**
 * 负责将 Item 投递到最新资讯区间的任务。
 * 
 * @author mixueqiang
 * @since Jul 8, 2016
 */
@Service
public class SectionLatestDeliveryTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(SectionLatestDeliveryTask.class);

  @Resource
  private EntityDao entityDao;

  public SectionLatestDeliveryTask() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 37, 61);
  }

  public SectionLatestDeliveryTask(EntityDao entityDao) {
    this.entityDao = entityDao;
  }

  @Override
  public void run() {
    Map<String, Object> condition = new HashMap<String, Object>();
    long begin = System.currentTimeMillis() - 6 * 60 * 60 * 1000L;
    Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
    offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);

    List<Entity> entities = entityDao.findByOffset("section_24h", condition, offsets, 1, PageNumberUtils.PAGE_SIZE_XLARGE, "publishTime", BaseDao.ORDER_OPTION_DESC);
    if (CollectionUtils.isEmpty(entities)) {
      LOG.warn("No items to deliver to section_latest.");
      return;
    }

    LOG.info("Get " + entities.size() + " items to deliver to section_latest.");

    long firstId = 0;
    long sectionId = Long.MAX_VALUE;
    List<Entity> firstSectionItems = new ArrayList<Entity>();
    List<Entity> sectionItems = new ArrayList<Entity>();
    for (int i = 0; i < entities.size(); i++) {
      Entity entity = entities.get(i);
      if (i < 6) {
        firstSectionItems.add(entity);
      }

      sectionItems.add(entity);

      // 获取publishTime 最小的一个作为sectionId.
      long publishTime = entity.getLong("publishTime");
      sectionId = sectionId > publishTime ? publishTime : sectionId;

      if (sectionItems.size() == 6) { // 保存一组Item.
        long currentId = saveIntoSection(sectionId, sectionItems);
        if (firstId == 0) {
          firstId = currentId;
        }

        sectionId = Long.MAX_VALUE;
        sectionItems = new ArrayList<Entity>();
      }
    }

    if (CollectionUtils.size(sectionItems) > 0) {
      if (CollectionUtils.size(firstSectionItems) >= 6) {
        sectionItems.addAll(firstSectionItems);
      }

      long currentId = saveIntoSection(sectionId, sectionItems);
      if (firstId == 0) {
        firstId = currentId;
      }
    }

    if (firstId > 0) {
      entityDao.updateByOffset("section_latest", "id", firstId, "status", Constants.STATUS_DISABLED);

    } else {
      long end = System.currentTimeMillis() - 6 * 60 * 60 * 1000L;
      entityDao.updateByOffset("section_latest", "sectionId", end, "status", Constants.STATUS_DISABLED);
    }
  }

  private long saveIntoSection(long sectionId, List<Entity> sectionItems) {
    if (CollectionUtils.isEmpty(sectionItems)) {
      return 0;
    }

    int count = 0;
    StringBuilder sb = new StringBuilder();
    for (Entity sectionItem : sectionItems) {
      long itemId = sectionItem.getLong("itemId");
      sb.append(itemId).append(",");

      count++;
      if (count >= 6) {
        break;
      }
    }
    sb.deleteCharAt(sb.length() - 1);

    long time = System.currentTimeMillis();
    try {
      Entity sectionLatest = new Entity("section_latest");
      sectionLatest.set("sectionId", sectionId).set("itemIds", sb.toString());
      sectionLatest.set("publishTime", time);
      sectionLatest.set("status", Constants.STATUS_ENABLED).set("createTime", time);
      sectionLatest = entityDao.saveAndReturn(sectionLatest);

      return sectionLatest.getId();

    } catch (Throwable t) {
      LOG.info("Failed to deliver " + sectionId + " to section_latest.", t);
      return 0;
    }
  }

}
