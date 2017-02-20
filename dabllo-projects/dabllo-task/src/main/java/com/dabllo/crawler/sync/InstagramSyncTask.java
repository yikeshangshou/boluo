package com.dabllo.crawler.sync;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabllo.dao.BaseDao;
import com.dabllo.dao.EntityDao;
import com.dabllo.model.Entity;
import com.dabllo.task.TaskScheduler;

/**
 * @author mixueqiang
 * @since Sep 17, 2014
 */
public class InstagramSyncTask extends AbstractSyncTask implements Runnable {
  private static final Log LOG = LogFactory.getLog(InstagramSyncTask.class);

  public InstagramSyncTask() {
    TaskScheduler.register(getClass().getSimpleName(), this, 13, 7 * 60);
  }

  public InstagramSyncTask(EntityDao entityDao) {
    this.entityDao = entityDao;
  }

  @Override
  public void finishTask(Entity source) {
    entityDao.update("source", "id", source.getId(), "updateTime", System.currentTimeMillis());
  }

  @Override
  public List<Entity> getTasks() {
    Map<String, Object> condition = new HashMap<String, Object>();
    condition.put("source", "instagram");
    condition.put("status", 1);
    List<Entity> sources = entityDao.find("source", condition, 1, 2, "updateTime", BaseDao.ORDER_OPTION_ASC);
    return sources;
  }

  @Override
  public int processTask(Entity source) throws Throwable {
    String url = source.getString("url");
    long topicId = source.getLong("topicId");

    Document document = Jsoup.parse(new URL(url), 3000);
    Elements elements = document.getElementsByTag("script");
    for (Element element : elements) {
      if (element.hasAttr("src")) {
        continue;
      }

      String html = element.html();
      if (!StringUtils.startsWith(html, "window._sharedData") || !StringUtils.contains(html, "\"code\"")) {
        continue;
      }

      long time = System.currentTimeMillis();
      String[] codes = StringUtils.substringsBetween(html, "\"code\": \"", "\",");
      for (String code : codes) {
        String feedUrl = "https://www.instagram.com/p/" + code + "/";

        if (!entityDao.exists("source_feed", "url", feedUrl)) {
          Entity entity = new Entity("source_feed");
          entity.set("source", source.getString("source")).set("sourceId", source.getId()).set("topicId", topicId).set("url", feedUrl);
          entity.set("status", 0).set("createTime", time);
          entityDao.save(entity);

          LOG.info("Get a new feed: " + feedUrl);
        }
      }

    }
    return 1;
  }

  @Override
  public void skipTask(Entity source) {
    entityDao.update("source", "id", source.getId(), "status", -1);
  }

}
