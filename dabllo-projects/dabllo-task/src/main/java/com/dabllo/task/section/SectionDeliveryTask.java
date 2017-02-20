package com.dabllo.task.section;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.dabllo.dao.EntityDao;
import com.dabllo.dao.mapper.ItemRowMapper;
import com.dabllo.model.Entity;
import com.dabllo.model.Item;
import com.dabllo.task.TaskScheduler;
import com.dabllo.util.PageNumberUtils;

/**
 * 负责将Item 中的信息投递到各个时间区块中。
 * 
 * @author mixueqiang
 * @since Jul 8, 2016
 */
@Service
public class SectionDeliveryTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(SectionDeliveryTask.class);

  @Resource
  private EntityDao entityDao;

  public SectionDeliveryTask() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 37, 15);
  }

  @Override
  public void run() {
    try {
      List<Item> items = entityDao.find("item", "publishTime", 0, ItemRowMapper.getInstance(), 1, PageNumberUtils.PAGE_SIZE_MEDIUM);
      if (CollectionUtils.isEmpty(items)) {
        return;
      }
      LOG.info("Get " + items.size() + " items to deliver.");

      long time = System.currentTimeMillis();
      for (Item item : items) {
        long itemId = item.getId();
        // 16小时和24小时排行榜。
        Entity entity = new Entity();
        entity.set("userId", item.getUserId()).set("itemId", itemId);
        entity.set("rank", 0).set("publishTime", time).set("createTime", time);

        entity.setModelName("section_24h");
        entityDao.save(entity);

        entity.put("section_3h_status", 0);
        entity.setModelName("section_16h");
        entityDao.save(entity);

        entityDao.update("item", "id", itemId, "publishTime", time);
        LOG.info("Item " + item.getId() + " has been delivered.");
      }

    } catch (Throwable t) {
      LOG.info("Failed to deliver item.", t);
    }

  }

}
