package com.dabllo.task.section;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.dabllo.util.Pair;

/**
 * @author mixueqiang
 * @since Jul 20, 2016
 */
@Service
public class Section1dGenerateTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(Section1dGenerateTask.class);

  @Resource
  private EntityDao entityDao;

  public Section1dGenerateTask() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 37, 5 * 60);
  }

  public Section1dGenerateTask(EntityDao entityDao) {
    this.entityDao = entityDao;
  }

  @Override
  public void run() {
    long time = System.currentTimeMillis() - 24 * 60 * 60 * 1000L;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String yesterday = dateFormat.format(new Date(time));
    if (entityDao.exists("section_1d", "date", yesterday)) {
      return;
    }

    LOG.info("Start to generate section_1d snapshot.");
    List<Entity> entities = null;

    try {
      Map<String, Object> condition = new HashMap<String, Object>();
      condition.put("status", Constants.STATUS_ENABLED);
      Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
      long begin = dateFormat.parse(yesterday).getTime();
      long end = begin + 24 * 60 * 60 * 1000L;
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_DESC), end);
      entities = entityDao.findByOffset("item", condition, offsets, 30, "rank", BaseDao.ORDER_OPTION_DESC);

    } catch (Throwable t) {
      LOG.error("Failed to get items for section_1d snapshot.", t);
      return;
    }

    if (CollectionUtils.isEmpty(entities)) {
      LOG.warn("No items for section_1d snapshot.");
      return;
    }

    LOG.info("Get " + entities.size() + " items for section_1d snapshot.");
    for (Entity entity : entities) {
      try {
        Entity section = new Entity("section_1d");
        section.set("date", yesterday).set("itemId", entity.getId()).set("userId", entity.getLong("userId")).set("rank", entity.getInt("rank"));
        section.set("publishTime", entity.getLong("publishTime")).set("createTime", System.currentTimeMillis());
        entityDao.save(section);

      } catch (Throwable t) {
        LOG.info("Failed to save section_1d snapshot.", t);
      }
    }
  }

}
