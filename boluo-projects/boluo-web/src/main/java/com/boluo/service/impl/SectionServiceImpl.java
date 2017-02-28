package com.boluo.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
import com.boluo.model.Section;
import com.boluo.service.SectionService;
import com.boluo.task.TaskScheduler;
import com.boluo.util.Pair;

/**
 * @author mixueqiang
 * @since Jul 7, 2016
 */
@Service
public class SectionServiceImpl extends BaseService implements SectionService, Runnable {
  private static final Log LOG = LogFactory.getLog(SectionServiceImpl.class);
  private static final long REFRESH_INTERVAL = 3 * MINUTE;
  private static List<Long> SECTION_LATEST_IDS = new CopyOnWriteArrayList<Long>();
  private static final Map<String, String> SECTION_TITLES = new HashMap<String, String>();
  private static final ConcurrentMap<String, Pair<Long, Section>> SECTIONS = new ConcurrentHashMap<String, Pair<Long, Section>>();
  private static final ConcurrentMap<Long, Long> USER_SECTION_OFFSETS = new ConcurrentHashMap<Long, Long>();

  static {
    SECTION_TITLES.put(SECTION_LATEST, "最新");
    SECTION_TITLES.put(SECTION_HOT, "热度上升最快");
    SECTION_TITLES.put(TIME_8H + TYPE_NEW, "最新");
    SECTION_TITLES.put(TIME_8H + TYPE_HOT, "热度上升最快");
    SECTION_TITLES.put(TIME_16H, "热度上升最快");
    SECTION_TITLES.put(TIME_24H, "24小时最热");
    SECTION_TITLES.put(TIME_1D, "昨日回顾");
    SECTION_TITLES.put(TIME_3D, "3天最热");
    SECTION_TITLES.put(TIME_7D, "7天最热");
  }

  public SectionServiceImpl() {
    TaskScheduler.register(getClass().getCanonicalName(), this, 17, 31);
  }

  @Override
  public Section getSection1(long userId) {
    Section section = getSectionT3h(userId);
    setSectionProperties(section);
    return section;
  }

  @Override
  public Section getSection2(long userId) {
    Section section = getSectionT16h(userId);

    setSectionProperties(section);
    return section;
  }

  @Override
  public Section getSection3(long userId) {
    Section section = getSectionT24h(userId);

    setSectionProperties(section);
    return section;
  }

  @Override
  public Section getSection4(long userId) {
    Section section = getSectionT3d(userId);

    setSectionProperties(section);
    return section;
  }

  @Override
  public Section getSectionT16h(long userId) {
    Section section = null;

    String name = TIME_16H;
    Pair<Long, Section> pair = SECTIONS.get(name);
    if (pair != null) {
      if (System.currentTimeMillis() - pair.left < REFRESH_INTERVAL) {
        section = pair.right;
      }
    }

    if (section == null) {
      long refreshTime = System.currentTimeMillis();
      section = new Section(SECTION_TITLES.get(name), refreshTime);

      Map<String, Object> condition = new HashMap<String, Object>();
      long end = System.currentTimeMillis();
      long begin = end - 16 * HOUR;
      Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_DESC), end);
      List<Entity> entities = entityDao.findByOffset("section_" + TIME_16H, condition, offsets, 12, "rank", BaseDao.ORDER_OPTION_DESC);

      List<Item> items = getItems(name, entities);
      section.setItems(items);

      SECTIONS.put(name, new Pair<Long, Section>(refreshTime, section));
    }

    return section;
  }

  @Override
  public Section getSectionT1d(long userId) {
    Section section = null;

    String name = TIME_1D;
    Pair<Long, Section> pair = SECTIONS.get(name);
    if (pair != null) {
      if (System.currentTimeMillis() - pair.left < REFRESH_INTERVAL) {
        section = pair.right;
      }
    }

    if (section == null) {
      long refreshTime = System.currentTimeMillis();
      section = new Section(SECTION_TITLES.get(name), refreshTime);

      Map<String, Object> condition = new HashMap<String, Object>();
      long yesterday = System.currentTimeMillis() - 1 * DAY;
      String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(yesterday));
      condition.put("date", date);
      List<Entity> entities = entityDao.find("section_" + TIME_1D, condition, 1, 6, "rank", BaseDao.ORDER_OPTION_DESC);
      List<Item> items = getItems(name, entities);
      section.setItems(items);

      SECTIONS.put(name, new Pair<Long, Section>(refreshTime, section));
    }

    return section;
  }

  @Override
  public Section getSectionT24h(long userId) {
    Section section = null;

    String name = TIME_24H;
    Pair<Long, Section> pair = SECTIONS.get(name);
    if (pair != null) {
      if (System.currentTimeMillis() - pair.left < REFRESH_INTERVAL) {
        section = pair.right;
      }
    }

    if (section == null) {
      long refreshTime = System.currentTimeMillis();
      section = new Section(SECTION_TITLES.get(name), refreshTime);

      Map<String, Object> condition = new HashMap<String, Object>();
      long end = System.currentTimeMillis();
      long begin = end - 24 * HOUR;
      Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_DESC), end);
      List<Entity> entities = entityDao.findByOffset("section_" + TIME_24H, condition, offsets, 12, "rank", BaseDao.ORDER_OPTION_DESC);

      List<Item> items = getItems(name, entities);
      section.setItems(items);

      SECTIONS.put(name, new Pair<Long, Section>(refreshTime, section));
    }

    return section;
  }

  @Override
  public Section getSectionT3d(long userId) {
    Section section = null;

    String name = TIME_3D;
    Pair<Long, Section> pair = SECTIONS.get(name);
    if (pair != null) {
      if (System.currentTimeMillis() - pair.left < REFRESH_INTERVAL) {
        section = pair.right;
      }
    }

    if (section == null) {
      long refreshTime = System.currentTimeMillis();
      section = new Section(SECTION_TITLES.get(name), refreshTime);

      Map<String, Object> condition = new HashMap<String, Object>();
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String yesterday = dateFormat.format(new Date(refreshTime - 1 * DAY));
      condition.put("date", yesterday);
      List<Entity> entities = entityDao.find("section_" + TIME_3D, condition, 1, 6, "rank", BaseDao.ORDER_OPTION_DESC);
      List<Item> items = getItems(name, entities);
      section.setItems(items);

      SECTIONS.put(name, new Pair<Long, Section>(refreshTime, section));
    }

    return section;
  }

  @Override
  public Section getSectionT3h(long userId) {
    String name = SECTION_LATEST;
    Section section = new Section();
    section.setTitle(SECTION_TITLES.get(name));

    if (CollectionUtils.isEmpty(SECTION_LATEST_IDS)) {
      updateSectionLatest();
    }

    if (CollectionUtils.isNotEmpty(SECTION_LATEST_IDS)) {
      long selectedSectionId = 0;
      // 寻找未读页。
      Long userSectionOffset = USER_SECTION_OFFSETS.get(userId);
      if (userSectionOffset == null) {
        userSectionOffset = 0L;
      }

      for (long sectionId : SECTION_LATEST_IDS) {
        if (sectionId < userSectionOffset) {
          selectedSectionId = sectionId;
          break;
        }
      }

      // 已经读完，重新循环取第1页。
      if (selectedSectionId == 0 && CollectionUtils.isNotEmpty(SECTION_LATEST_IDS)) {
        selectedSectionId = SECTION_LATEST_IDS.get(0);
      }

      if (selectedSectionId > 0) {
        Pair<Long, Section> pair = SECTIONS.get(SECTION_LATEST + "_" + selectedSectionId);
        section = pair.right;

        // 更新已读偏移。
        USER_SECTION_OFFSETS.put(userId, selectedSectionId);

      } else {
        LOG.warn("[Cache] Can not get section latest from cache for user: " + userId);
      }
    }

    // 缓存没有数据时，随机取。
    if (CollectionUtils.isEmpty(section.getItems())) {
      LOG.warn("[Cache] Section " + name + " cache miss!");

      Map<String, Object> condition = new HashMap<String, Object>();
      long end = System.currentTimeMillis();
      long begin = end - 6 * HOUR;
      Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_DESC), end);
      List<Entity> entities = entityDao.findByOffset("section_" + TIME_24H, condition, offsets, 18, "publishTime", BaseDao.ORDER_OPTION_DESC);

      List<Item> items = getItems(name, entities);
      section.setItems(items);
    }

    return section;
  }

  @Override
  public Section getSectionT7d(long userId) {
    Section section = null;

    String name = TIME_7D;
    Pair<Long, Section> pair = SECTIONS.get(name);
    if (pair != null) {
      if (System.currentTimeMillis() - pair.left < REFRESH_INTERVAL) {
        section = pair.right;
      }
    }

    if (section == null) {
      long refreshTime = System.currentTimeMillis();
      section = new Section(SECTION_TITLES.get(name), refreshTime);

      Map<String, Object> condition = new HashMap<String, Object>();
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String yesterday = dateFormat.format(new Date(refreshTime - 1 * DAY));
      condition.put("date", yesterday);
      List<Entity> entities = entityDao.find("section_" + TIME_7D, condition, 1, 6, "rank", BaseDao.ORDER_OPTION_DESC);
      List<Item> items = getItems(name, entities);
      section.setItems(items);

      SECTIONS.put(name, new Pair<Long, Section>(refreshTime, section));
    }

    return section;
  }

  @Override
  public Section getSectionT8hHot(long userId) {
    String name = TIME_8H + TYPE_HOT;
    Section section = null;

    Pair<Long, Section> pair = SECTIONS.get(name);
    if (pair != null) {
      if (System.currentTimeMillis() - pair.left < REFRESH_INTERVAL) {
        section = pair.right;
      }
    }

    if (section == null) {
      long refreshTime = System.currentTimeMillis();
      section = new Section(SECTION_TITLES.get(name), refreshTime);

      Map<String, Object> condition = new HashMap<String, Object>();
      long end = System.currentTimeMillis();
      long begin = end - 8 * HOUR;
      Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
      offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_DESC), end);
      List<Entity> entities = entityDao.findByOffset("section_" + TIME_16H, condition, offsets, 12, "rank", BaseDao.ORDER_OPTION_DESC);

      List<Item> items = getItems(name, entities);
      section.setItems(items);

      SECTIONS.put(name, new Pair<Long, Section>(refreshTime, section));
    }

    return section;
  }

  @Override
  public Section getSectionT8hNew(long userId) {
    String name = TIME_8H + TYPE_NEW;
    long refreshTime = System.currentTimeMillis();
    Section section = new Section(SECTION_TITLES.get(name), refreshTime);

    Map<String, Object> condition = new HashMap<String, Object>();
    long end = System.currentTimeMillis();
    long begin = end - 8 * HOUR;
    Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
    offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_ASC), begin);
    offsets.put(new Pair<String, String>("publishTime", BaseDao.ORDER_OPTION_DESC), end);
    List<Entity> entities = entityDao.findByOffset("section_" + TIME_16H, condition, offsets, 6, "publishTime", BaseDao.ORDER_OPTION_DESC);

    List<Item> items = getItems(name, entities);
    section.setItems(items);

    return section;
  }

  @Override
  public boolean resetCache(String time) {
    if (StringUtils.equals(SECTION_LATEST, time)) {
      SECTION_LATEST_IDS.clear();
      return true;

    } else if (StringUtils.equals(SECTION_HOT, time)) {
      SECTIONS.remove(TIME_16H);
      return true;

    } else if (StringUtils.equals(TIME_8H + TYPE_NEW, time)) {
      return true;

    } else if (StringUtils.equals(TIME_8H + TYPE_HOT, time)) {
      SECTIONS.remove(TIME_8H + TYPE_HOT);
      return true;

    } else if (StringUtils.equals(TIME_16H, time)) {
      SECTIONS.remove(TIME_16H);
      return true;

    } else if (StringUtils.equals(TIME_24H, time)) {
      SECTIONS.remove(TIME_24H);
      return true;

    } else if (StringUtils.equals(TIME_1D, time)) {
      SECTIONS.remove(TIME_1D);
      return true;

    } else if (StringUtils.equals(TIME_3D, time)) {
      SECTIONS.remove(TIME_3D);
      return true;

    } else if (StringUtils.equals(TIME_7D, time)) {
      SECTIONS.remove(TIME_7D);
      return true;
    }

    return false;
  }

  @Override
  public void run() {
    // 更新最新资讯（6小时）缓存。
    updateSectionLatest();
  }

  private List<Item> getItems(String sectionName, List<Entity> entities) {
    if (CollectionUtils.isEmpty(entities)) {
      LOG.warn("Can not get items on " + sectionName);
    }

    List<Item> items = new ArrayList<Item>();
    for (Entity entity : entities) {
      long itemId = entity.getLong("itemId");

      if (isSelected(sectionName, itemId)) { // 排除掉已经选中的资讯。
        continue;
      }

      Item item = entityDao.get("item", itemId, ItemRowMapper.getInstance());
      if (item != null && item.getStatus() == Constants.STATUS_ENABLED) {
        items.add(item);
      }

      if (items.size() >= 6) {
        break;
      }
    }

    return items;
  }

  private List<Item> getSectionCacheItems(String... sectionNames) {
    List<Item> items = new ArrayList<Item>();
    for (String sectionName : sectionNames) {
      Pair<Long, Section> pair = SECTIONS.get(sectionName);
      if (pair != null && pair.right != null) {
        items.addAll(pair.right.getItems());
      }
    }

    return items;
  }

  /**
   * 检查当前项目是否已经被选择。
   */
  private boolean isSelected(String sectionName, long itemId) {
    if (StringUtils.equals(TIME_16H, sectionName) || StringUtils.equals(TIME_8H + TYPE_HOT, sectionName)) {
      List<Item> items = getSectionCacheItems(TIME_24H);
      if (CollectionUtils.isEmpty(items)) {
        return false;
      }

      for (Item item : items) {
        if (itemId == item.getId()) {
          return true;
        }
      }

    } else if (StringUtils.equals(SECTION_LATEST, sectionName)) {
      List<Item> items = getSectionCacheItems(TIME_24H, TIME_8H + TYPE_HOT);
      if (CollectionUtils.isEmpty(items)) {
        return false;
      }

      for (Item item : items) {
        if (itemId == item.getId()) {
          return true;
        }
      }
    }

    return false;
  }

  private void setSectionProperties(Section section) {
    section.getProperties().put("menu", new String[] { "more" });

    if (CollectionUtils.isEmpty(section.getItems())) {
      return;
    }

    for (Item item : section.getItems()) {
      if (item.getHot() == 1) {
        item.getProperties().put("hot", 1);
      }
    }
  }

  private void updateSectionLatest() {
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("status", Constants.STATUS_ENABLED);
    long refreshTime = System.currentTimeMillis();
    long begin = refreshTime - 6 * HOUR;
    Map<Pair<String, String>, Object> offsets = new HashMap<Pair<String, String>, Object>();
    offsets.put(new Pair<String, String>("sectionId", BaseDao.ORDER_OPTION_ASC), begin);
    List<Entity> entities = entityDao.findByOffset("section_latest", condition, offsets, 1, 60, "sectionId", BaseDao.ORDER_OPTION_DESC);
    if (CollectionUtils.isEmpty(entities)) {
      LOG.info("[Cache] Update section_latest cache: 0 sections.");
      return;
    }

    String sectionTitle = SECTION_TITLES.get(SECTION_LATEST);
    LOG.info("[Cache] Update section_latest cache: " + entities.size() + " sections.");
    List<Long> sectionLatestIds = new CopyOnWriteArrayList<Long>();
    for (Entity entity : entities) {
      long sectionId = entity.getLong("sectionId");
      if (!sectionLatestIds.contains(sectionId)) {
        sectionLatestIds.add(sectionId);
      }

      String itemIds = entity.getString("itemIds");
      String sectionName = SECTION_LATEST + "_" + sectionId;
      Section section = new Section(sectionTitle, refreshTime);

      for (String itemId : StringUtils.split(itemIds, ',')) {
        long id = Long.valueOf(itemId);
        Item item = entityDao.get("item", id, ItemRowMapper.getInstance());
        if (item != null && item.getStatus() > Constants.STATUS_DISABLED) {
          section.getItems().add(item);
        }
      }
      SECTIONS.put(sectionName, new Pair<Long, Section>(refreshTime, section));
    }

    SECTION_LATEST_IDS = sectionLatestIds;
  }

}
